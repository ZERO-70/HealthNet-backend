# Deploying HealthNet

## Heroku with the $13/month student credit

Heroku has **no first-party MySQL**. The options and what they cost against a
$13/month credit:

| setup | monthly | verdict |
|---|---|---|
| Basic dyno $7 + **Aiven free MySQL $0** | **$7** | **recommended** — no code changes, $6 headroom |
| Basic dyno $7 + Heroku Postgres Essential-0 $5 | $12 | fits, but needs a full MySQL→Postgres port |
| Eco dyno $5 + JawsDB Leopard $10 | $15 | over budget |
| any dyno + JawsDB Kitefin free | — | 5 MB storage; unusable here |

Use a **Basic dyno ($7)**, not Eco ($5). Eco dynos sleep after 30 minutes idle,
so a link on a resume takes ~30s to respond on the first click. Basic never
sleeps, and the credit covers it either way.

**Do not port to Postgres to save $2.** It means rewriting the schema and
auditing all 16 repository classes: `AUTO_INCREMENT`→`IDENTITY`,
`LONGBLOB`→`BYTEA`, dropping `ENGINE=InnoDB`, replacing MySQL backtick quoting
around reserved names like `` `date` `` and `` `timestamp` ``, and re-checking
the `GeneratedKeyHolder` inserts. It is a real risk of subtle breakage for no
functional gain.

### Deploying the app

The repo has a `heroku.yml` so Heroku builds the Dockerfile directly:

```bash
heroku stack:set container -a <app-name>
git push heroku main
```

The Java buildpack path is avoided deliberately — it runs `mvn clean install`
during the build, which executes `HealthNetApplicationTests` (a `@SpringBootTest`
needing a database) and fails.

Set config vars:

```bash
heroku config:set \
  DB_URL="jdbc:mysql://<host>:<port>/<db>?sslMode=REQUIRED&serverTimezone=UTC" \
  DB_USERNAME=<user> DB_PASSWORD=<password> \
  DB_POOL_MAX=5 DB_POOL_MIN_IDLE=1 \
  JWT_SECRET="$(openssl rand -base64 32)" \
  AI_ENABLED=false -a <app-name>
```

### Pick the Aiven region to match the dyno

This matters more than it sounds. The app issues many small queries per request,
so every millisecond of round-trip is multiplied. Heroku's US common runtime is
in AWS `us-east-1` (Virginia) and EU is `eu-west-1` (Ireland) — create the Aiven
service in the **same** region. A cross-continent pairing can turn a page load
into seconds.

### Aiven free-tier limits (verified)

- 1 GB storage, 1 GB RAM, 1 CPU, `max_connections` = **76**
- No credit card, free indefinitely, no SLA
- **Idle services get powered off** after a period of no activity, with a
  notification first; they can be turned back on at any time.

That last point matters for a resume link that may sit unvisited for months. A
Basic dyno does not sleep, and `DB_POOL_MIN_IDLE=1` keeps a connection open
against the database continuously — which is why the setting above is `1` and
not `0`.

---

# Free (no-budget) alternative

If the credit runs out, the same app runs at zero cost on Koyeb + Aiven.

## Recommended free stack

| piece    | service                     | why |
|----------|-----------------------------|-----|
| database | **Aiven for MySQL** free tier | 1 GB storage, 1 GB RAM, always-free, **no credit card**, and it is real MySQL — no schema rewrite |
| backend  | **Koyeb** free web service    | 512 MB RAM, deploys the Dockerfile, and does **not** sleep |
| frontend | **GitHub Pages**              | already wired up — `package.json` has `predeploy`/`deploy` via `gh-pages` |

**Render** is the better-known backend option and has a permanent free tier, but
it asks for a card and free services **sleep after inactivity**, taking ~50s to
wake. For a resume link someone clicks once, that first impression is bad —
prefer Koyeb, or accept the cold start.

Avoid Postgres-only free tiers (Render Postgres, Neon, Supabase). This app uses
raw JDBC with MySQL-specific SQL (`AUTO_INCREMENT`, `LONGBLOB`, `ENGINE=InnoDB`),
so switching engines means rewriting every repository class.

## Steps

### 1. Database (Aiven)

Create a free MySQL service, then load the schema. Aiven gives you host, port,
user, password and a CA certificate.

```bash
mysql -h <host> -P <port> -u <user> -p --ssl-mode=REQUIRED <dbname> < db/01_schema.sql
mysql -h <host> -P <port> -u <user> -p --ssl-mode=REQUIRED <dbname> < db/02_seed.sql
```

### 2. Backend (Koyeb)

Point it at the GitHub repo; it builds the `Dockerfile` automatically. Set these
environment variables:

```
DB_URL=jdbc:mysql://<host>:<port>/<dbname>?sslMode=REQUIRED&serverTimezone=UTC
DB_USERNAME=<user>
DB_PASSWORD=<password>
DB_POOL_MAX=3
DB_POOL_MIN_IDLE=1
JWT_SECRET=<output of: openssl rand -base64 32>
AI_ENABLED=false
```

Then seed the accounts against the deployed URL:

```bash
./scripts/seed-users.sh https://<your-app>.koyeb.app
```

### 3. Frontend (GitHub Pages)

`REACT_APP_API_BASE_URL` is inlined at **build** time, so it must be set before
building:

```bash
cd ../Healthnet-frontend
REACT_APP_API_BASE_URL=https://<your-app>.koyeb.app npm run build
npm run deploy
```

Add `"homepage": "https://<user>.github.io/<repo>"` to `package.json` first, or
the built asset paths will 404.

## Gotchas specific to this project

1. **Set `JWT_SECRET`.** This matters far more on a free tier than locally.
   Without it the app generates a new signing key on every startup, and free
   instances restart or wake constantly — so every user gets logged out each
   time. It is a one-line env var that prevents a bug that looks random.

2. **Keep the connection pool modest.** Aiven's free tier allows 76 connections,
   so the defaults (max 10, 5 idle) are not a problem there — but they leave
   little room if you ever scale to several dynos, and other providers cap much
   lower. `DB_POOL_MAX=5`/`MIN_IDLE=1` is a safe fit for one small instance.

3. **SSL is required.** Managed providers reject plaintext connections. Use
   `sslMode=REQUIRED` in the JDBC URL — the local default is `useSSL=false`,
   which will fail against Aiven.

4. **Watch the 1 GB limit.** Profile images (`person.image`) and record
   attachments (`medical_record_attachments.file_data`) are stored as `LONGBLOB`
   **inside the database**, not on disk. A handful of uploads can consume a free
   tier. For a demo, avoid uploading large files.

5. **Tighten CORS before sharing.** `SecurityConfig` and `CorsConfig` both allow
   all origins. It will work as-is, but restrict it to the Pages domain.

6. **Change the demo passwords**, or delete the demo accounts, once the link is
   public.

## Fully free alternative: one VM

Oracle Cloud's Always Free tier includes an ARM VM (up to 4 cores / 24 GB RAM)
that does not expire. `docker-compose.yml` in this repo runs the whole stack
there — app and MySQL together, no storage cap, no cold starts. It needs a credit
card for identity verification and more setup (firewall, TLS), but it is the most
capable free option, and "deployed and operated it on my own VM" reads well on a
resume.
