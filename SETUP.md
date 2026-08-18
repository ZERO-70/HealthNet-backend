# HealthNet — local setup

The original MySQL server and the AI query service that this project talked to
are both gone. The database schema has been reconstructed from the SQL in
`src/main/java/com/server/HealthNet/Repository/*.java`, and the AI integration
is now behind a feature flag that is off by default.

## Requirements

- Java 17+ (Java 21 works)
- Docker + Docker Compose
- Node 18+ (for the frontend)

## 1. Start the database

```bash
docker compose up -d
```

This starts MySQL 8 on host port **3307** and, on first start only, runs
everything in `db/`:

| file            | contents                                   |
|-----------------|--------------------------------------------|
| `01_schema.sql` | all 16 tables, FKs and indexes              |
| `02_seed.sql`   | departments and inventory reference data    |

To rebuild the database from scratch (this **deletes all data**):

```bash
docker compose down -v && docker compose up -d
```

## 2. Run the backend

```bash
./mvnw spring-boot:run
```

Listens on `http://localhost:8081`. Configuration comes from environment
variables with local-friendly defaults — see `.env.example`. Nothing secret is
committed anymore.

## 3. Create demo accounts

With the backend running:

```bash
./scripts/seed-users.sh              # all demo accounts
./scripts/seed-users.sh --admin-only # just the admin
```

Accounts go through `/user_authentication/register` rather than SQL because the
app BCrypt-hashes passwords at registration time.

The admin is registered with a **null `person_id`**. `POST /person` is itself
ADMIN-only, and the only public person-creating endpoints are `/doctor` and
`/patient` — using either would place the admin in the doctor or patient
listings. The column is nullable, so the account works without one.

| username   | password    | role    |
|------------|-------------|---------|
| `admin`    | `admin123`  | ADMIN   |
| `doctor1`  | `doctor123` | DOCTOR  |
| `doctor2`  | `doctor123` | DOCTOR  |
| `patient1` | `patient123`| PATIENT |
| `patient2` | `patient123`| PATIENT |

These are throwaway development credentials. Do not ship them to a public host.

## 4. Run the frontend

```bash
cd ../Healthnet-frontend
npm install
cp .env.example .env.local
npm start
```

Serves on `http://localhost:3000` and talks to the backend via
`REACT_APP_API_BASE_URL`. Note that Create-React-App inlines that value at
**build** time, so it must be set before `npm run build` when deploying.

## The AI / chat feature

The backend used to call `http://159.89.49.64:7898` on three paths:

| path                  | used by            | purpose                          |
|-----------------------|--------------------|----------------------------------|
| `/api/query`          | `ChatService`      | the chat assistant               |
| `/api/medical-advice` | `SuggestionService`| daily per-patient advice          |
| `/api/email`          | `SuggestionService`| emailing a suggestion            |

That server no longer exists, so the integration is disabled by default:

```properties
healthnet.ai.enabled=false
```

While disabled:

- Chat endpoints return a clear "AI assistant is currently unavailable" message
  immediately, instead of blocking for the old 10-minute timeout. Chat history is
  still recorded so the conversation view stays coherent.
- The daily suggestion job is skipped entirely. This matters: that job starts by
  deleting **every** row in `suggestion` before refetching, so letting it run
  without a reachable AI service would wipe the table and repopulate nothing.

To attach a replacement, implement at least the chat contract:

```
POST <AI_BASE_URL>/api/query
{ "query": "...", "role": "PATIENT", "patient_id": 1, "doctor_id": null, "model": "FAST" }
->
{ "response": "..." }
```

then set `AI_ENABLED=true` and `AI_BASE_URL=...`.

## Before hosting this publicly

1. **Rotate anything reused.** The old database password was committed to git
   history (`src/main/resources/application.properties`, 4 commits). The server
   is gone, but if that password was reused anywhere, change it there.
2. **Set `JWT_SECRET`.** Without it a new signing key is generated on every
   startup, so every token is invalidated on restart and sessions break across
   multiple instances.
3. **Tighten CORS.** `SecurityConfig` and `CorsConfig` both allow all origins.
4. **Review the demo accounts** — delete or change them.
