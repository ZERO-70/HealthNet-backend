#!/usr/bin/env bash
# Loads db/01_schema.sql and db/02_seed.sql into whatever database .env points at.
#
# Reads DB_URL / DB_USERNAME / DB_PASSWORD from .env so credentials stay out of
# the shell history and out of the repo (.env is gitignored).
#
# Usage: ./scripts/load-schema.sh [extra.sql ...]
set -euo pipefail

cd "$(dirname "$0")/.."

if [ ! -f .env ]; then
  echo "No .env found. Copy .env.example to .env and fill in the Aiven values."
  exit 1
fi

set -a
# shellcheck disable=SC1091
. ./.env
set +a

DRIVER=$(find "$HOME/.m2" -name 'mysql-connector-j-*.jar' 2>/dev/null | sort | tail -1)
if [ -z "$DRIVER" ]; then
  echo "MySQL JDBC driver not found in ~/.m2. Run ./mvnw compile first."
  exit 1
fi

FILES=("$@")
if [ ${#FILES[@]} -eq 0 ]; then
  FILES=(db/01_schema.sql db/02_seed.sql)
fi

exec java -cp "$DRIVER" scripts/DbInit.java "${FILES[@]}"
