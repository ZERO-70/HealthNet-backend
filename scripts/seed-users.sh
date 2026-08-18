#!/usr/bin/env bash
# Seeds demo accounts into a running HealthNet backend.
#
# Accounts must be created through the API, not SQL: the app BCrypt-hashes
# passwords on registration, and a hash cannot be written by hand into the
# schema without hardcoding a specific cost factor.
#
# Usage:
#   ./scripts/seed-users.sh [BASE_URL]              # doctors, patients and admin
#   ./scripts/seed-users.sh --admin-only [BASE_URL] # just the admin account
#
# Default BASE_URL is http://localhost:8081
set -euo pipefail

ADMIN_ONLY=false
if [ "${1:-}" = "--admin-only" ]; then
  ADMIN_ONLY=true
  shift
fi

BASE="${1:-http://localhost:8081}"

echo "Seeding demo users into ${BASE}"

# Wait for the backend to answer. /user_authentication/exists/... is public and
# returns a plain boolean, so it makes a clean readiness probe.
for i in $(seq 1 30); do
  if curl -fsS "${BASE}/user_authentication/exists/__probe__" >/dev/null 2>&1; then break; fi
  [ "$i" = 30 ] && { echo "Backend not reachable at ${BASE}"; exit 1; }
  sleep 2
done

# create_person_via <endpoint> <json>  -> echoes the new person id
create_person_via() {
  curl -fsS -X POST "${BASE}/$1" -H 'Content-Type: application/json' -d "$2"
}

register() { # username password role personId
  local code
  code=$(curl -sS -o /tmp/healthnet_reg.out -w '%{http_code}' \
    -X POST "${BASE}/user_authentication/register" \
    -H 'Content-Type: application/json' \
    -d "{\"username\":\"$1\",\"password\":\"$2\",\"role\":\"$3\",\"personId\":$4,\"subscription\":\"$5\"}")
  if [ "$code" = "201" ]; then
    echo "  ✓ $3 $1 / $2  (person_id=$4)"
  else
    echo "  ✗ $1 -> HTTP $code: $(cat /tmp/healthnet_reg.out)"
  fi
}

if [ "$ADMIN_ONLY" = false ]; then
echo "Creating doctors..."
DOC1=$(create_person_via doctor '{"name":"Dr. Ayesha Khan","gender":"Female","age":41,"birthdate":"1985-04-12","contact_info":"ayesha.khan@healthnet.test","address":"12 Clinic Road","specialization":"Cardiology"}')
DOC2=$(create_person_via doctor '{"name":"Dr. Imran Ali","gender":"Male","age":37,"birthdate":"1989-09-03","contact_info":"imran.ali@healthnet.test","address":"8 Hospital Ave","specialization":"Neurology"}')

echo "Creating patients..."
PAT1=$(create_person_via patient '{"name":"Sara Ahmed","gender":"Female","age":29,"birthdate":"1997-02-18","contact_info":"sara.ahmed@healthnet.test","address":"44 Garden Town","weight":"62","height":"165"}')
PAT2=$(create_person_via patient '{"name":"Bilal Hussain","gender":"Male","age":54,"birthdate":"1972-11-25","contact_info":"bilal.hussain@healthnet.test","address":"7 Model Colony","weight":"81","height":"178"}')

echo "Registering accounts..."
register doctor1 doctor123 DOCTOR  "$DOC1" DEFAULT
register doctor2 doctor123 DOCTOR  "$DOC2" DEFAULT
register patient1 patient123 PATIENT "$PAT1" PLUS
register patient2 patient123 PATIENT "$PAT2" DEFAULT
fi

# Bootstrapping the admin takes three steps because POST /persons is itself
# ADMIN-only (chicken-and-egg), and the only public person-creating endpoints are
# /doctor and /patient — which would put the admin in those listings.
#
#   1. register with a null person_id (person_id is nullable)
#   2. log in as that admin, and use its own rights to create a bare person
#   3. link the person back to the account
#
# Step 3 matters: the frontend calls /persons/getmine for the profile, which
# 404s while the account has no person attached.
echo "Creating admin..."
register admin admin123 ADMIN null DEFAULT

ADMIN_TOKEN=$(curl -sS -X POST "${BASE}/user_authentication/login" \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin123"}' || true)

if [ -n "$ADMIN_TOKEN" ]; then
  curl -fsS -X POST "${BASE}/persons" -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H 'Content-Type: application/json' \
    -d '{"name":"System Admin","gender":"Other","age":30,"birthdate":"1996-01-01","contact_info":"admin@healthnet.test","address":"HealthNet HQ"}' \
    >/dev/null 2>&1 || true

  ADMIN_PID=$(curl -fsS "${BASE}/persons" -H "Authorization: Bearer $ADMIN_TOKEN" 2>/dev/null | python3 -c "
import sys, json
try:
    rows = json.load(sys.stdin)
except Exception:
    sys.exit()
m = [r for r in rows if r.get('name') == 'System Admin' and r.get('id')]
print(m[-1]['id'] if m else '')
" 2>/dev/null || true)

  if [ -n "${ADMIN_PID:-}" ]; then
    # Note: this PUT re-hashes the password it is given, so the plaintext is correct here.
    curl -fsS -X PUT "${BASE}/user_authentication/admin" -H "Authorization: Bearer $ADMIN_TOKEN" \
      -H 'Content-Type: application/json' \
      -d "{\"username\":\"admin\",\"password\":\"admin123\",\"role\":\"ADMIN\",\"personId\":${ADMIN_PID},\"subscription\":\"DEFAULT\"}" \
      >/dev/null 2>&1 && echo "  ✓ linked admin to person_id=${ADMIN_PID}"
  fi
fi

# Staff accounts need an admin token: /user_authentication/register refuses to
# create a STAFF role unless the caller is already an ADMIN, and POST /staff is
# ADMIN-only too. So this runs after the admin exists.
if [ -n "${ADMIN_TOKEN:-}" ] && [ "$ADMIN_ONLY" = false ]; then
  echo "Creating staff..."
  STAFF1=$(curl -sS -X POST "${BASE}/staff" -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H 'Content-Type: application/json' \
    -d '{"name":"Numan Riaz","gender":"Male","age":34,"birthdate":"1992-06-10","contact_info":"numan@healthnet.test","address":"3 Hospital Lane","profession":"Management"}' 2>/dev/null || true)

  if [ -n "${STAFF1:-}" ] && [ "$STAFF1" -eq "$STAFF1" ] 2>/dev/null && [ "$STAFF1" -gt 0 ] 2>/dev/null; then
    code=$(curl -sS -o /tmp/healthnet_reg.out -w '%{http_code}' \
      -X POST "${BASE}/user_authentication/register" \
      -H "Authorization: Bearer $ADMIN_TOKEN" -H 'Content-Type: application/json' \
      -d "{\"username\":\"numan\",\"password\":\"numan123\",\"role\":\"STAFF\",\"personId\":${STAFF1},\"subscription\":\"DEFAULT\"}")
    if [ "$code" = "201" ]; then
      echo "  ✓ STAFF numan / numan123  (person_id=${STAFF1})"
    else
      echo "  ✗ numan -> HTTP $code: $(cat /tmp/healthnet_reg.out)"
    fi
  else
    echo "  ! Skipped staff: could not create the staff person record."
  fi
fi

# Treatments reference doctors, so they are created here rather than in
# db/02_seed.sql — at schema-load time no doctors exist yet.
#
# This matters beyond demo data: medical_records.treatment_id is a foreign key,
# so with an empty treatment table every attempt to save a medical record that
# names a treatment fails on a constraint violation.
if [ -n "${ADMIN_TOKEN:-}" ] && [ "$ADMIN_ONLY" = false ]; then
  echo "Creating treatments..."
  add_treatment() {
    code=$(curl -sS -o /dev/null -w '%{http_code}' -X POST "${BASE}/treatement" \
      -H "Authorization: Bearer $ADMIN_TOKEN" -H 'Content-Type: application/json' -d "$1")
    [ "$code" = "200" ] || [ "$code" = "201" ] && echo "  ✓ $2" || echo "  ✗ $2 (HTTP $code)"
  }
  add_treatment "{\"name\":\"General Consultation\",\"doctor_id\":${DOC1},\"department_id\":7}" "General Consultation"
  add_treatment "{\"name\":\"Cardiac Assessment\",\"doctor_id\":${DOC1},\"department_id\":1}" "Cardiac Assessment"
  add_treatment "{\"name\":\"Neurological Evaluation\",\"doctor_id\":${DOC2},\"department_id\":2}" "Neurological Evaluation"
  add_treatment "{\"name\":\"Physiotherapy Session\",\"doctor_id\":${DOC2},\"department_id\":3}" "Physiotherapy Session"
  add_treatment "{\"name\":\"Emergency Triage\",\"doctor_id\":${DOC1},\"department_id\":5}" "Emergency Triage"
fi

echo
echo "Done. Log in at ${BASE}/user_authentication/login"
