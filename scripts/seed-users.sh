#!/usr/bin/env bash
# Seeds demo accounts into a running HealthNet backend.
#
# Accounts must be created through the API, not SQL: the app BCrypt-hashes
# passwords on registration, and a hash cannot be written by hand into the
# schema without hardcoding a specific cost factor.
#
# Usage:  ./scripts/seed-users.sh [BASE_URL]
# Default BASE_URL is http://localhost:8081
set -euo pipefail

BASE="${1:-http://localhost:8081}"
# Allow "sudo docker" via DOCKER="sudo docker" if your user isn't in the docker group.
DOCKER="${DOCKER:-docker}"

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

# The admin needs a bare person row, and there is no public endpoint for one,
# so it goes in via SQL. Non-fatal: if docker isn't reachable the rest still stands.
echo "Creating admin..."
ADMIN_ID=$($DOCKER compose exec -T mysql mysql -N -B \
  -u"${MYSQL_USER:-healthnet}" -p"${MYSQL_PASSWORD:-healthnet_pass}" "${MYSQL_DATABASE:-healthnetstorage}" \
  -e "INSERT INTO person (name, gender, age, contact_info) VALUES ('System Admin','Other',30,'admin@healthnet.test'); SELECT LAST_INSERT_ID();" 2>/dev/null | tail -1 || true)

if [ -n "${ADMIN_ID:-}" ] && [ "$ADMIN_ID" -eq "$ADMIN_ID" ] 2>/dev/null; then
  register admin admin123 ADMIN "$ADMIN_ID" DEFAULT
else
  echo "  ! Skipped admin: could not reach the DB via '$DOCKER compose exec'."
  echo "    Re-run just this part with:  DOCKER=\"sudo docker\" $0 $BASE"
fi

echo
echo "Done. Log in at ${BASE}/user_authentication/login"
