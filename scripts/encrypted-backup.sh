#!/usr/bin/env bash
set -euo pipefail
: "${BACKUP_DATABASE_URL:?Set BACKUP_DATABASE_URL in the environment}"
: "${BACKUP_CERT_FILE:?Set BACKUP_CERT_FILE to the public certificate path}"
output="${1:?Specify the encrypted output path}"
[ -r "$BACKUP_CERT_FILE" ] || { echo 'Backup certificate cannot be read' >&2; exit 1; }
umask 077
tmp="${output}.tmp.$$"
trap 'rm -f "$tmp"' EXIT
pg_dump --dbname="$BACKUP_DATABASE_URL" --schema=keepit --format=custom --no-owner --no-acl \
  | openssl cms -encrypt -aes256 -binary -outform DER -out "$tmp" "$BACKUP_CERT_FILE"
test -s "$tmp"
mv "$tmp" "$output"
