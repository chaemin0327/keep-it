# 배포 준비 (미완료)

실제 배포 전에 PostgreSQL, 이메일 발송 계정과 HTTPS 주소가 필요합니다. 이 문서는 예시 설정이며 운영 준비 완료를 의미하지 않습니다.

## PostgreSQL

- 별도의 PostgreSQL DB를 준비하고, 관리자가 `postgres-schema.sql`을 **새 빈 DB에 한 번만** 실행합니다. 이 SQL은 기존 데이터를 옮기는 도구가 아닙니다.
- Supabase 사용 시 `keepit` 스키마를 Data API에 노출하지 마세요. 스키마 접근 권한을 제한하고 백엔드 전용 DB 계정을 따로 만들어 필요한 권한만 주세요. 공개 anon/authenticated 역할에 접근 권한을 주지 마세요.
- DB 연결은 SSL 및 서버 인증서를 검증해야 합니다. Supabase 대시보드의 연결 정보와 CA 인증서로 JDBC URL의 `sslmode=verify-full` 및 `sslrootcert`를 구성하세요. 배포 플랫폼에서 IPv4만 제공하면 세션 풀러를 사용합니다.
- 다음 설정을 배포 서비스의 비공개 환경 변수로 지정합니다. 값을 저장소나 채팅에 적지 마세요.

```
SPRING_PROFILES_ACTIVE=production
KEEPIT_DATABASE_URL=jdbc:postgresql://.../postgres?sslmode=verify-full&sslrootcert=...
KEEPIT_DATABASE_USER=...
KEEPIT_DATABASE_PASSWORD=...
KEEPIT_MAIL_API_KEY=...
KEEPIT_MAIL_FROM=...
KEEPIT_PUBLIC_URL=https://실제-사이트-주소
GEMINI_API_KEY=...
```

운영 프로필은 생성된 테이블을 검증만 하며, 운영 서버는 HTTPS를 제공하는 신뢰할 수 있는 프록시 뒤에 둡니다. 메일 발송은 Brevo HTTPS API를 사용합니다. 발신 주소 인증, 트랜잭션 메일 활성화와 실제 이메일 송수신 검증이 필요합니다. Render 무료 서버는 일반 SMTP 포트를 차단합니다.

## 기능과 남은 작업

- 이메일 인증 및 비밀번호 재설정 링크는 30분 동안 한 번만 쓸 수 있습니다. 비밀번호를 변경하면 기존 로그인 세션이 무효화됩니다.
- 운영 환경의 로그인 및 AI 요청 횟수 제한은 PostgreSQL에 저장되어 서버 간 공유됩니다. 프록시 뒤에서 모든 사용자가 같은 주소로 보일 수 있으므로 실제 호스팅 환경에서 검증해야 합니다.
- `내 기록 백업`으로 경험과 프로필을 JSON으로 내려받고 `백업 가져오기`로 경험을 현재 계정에 추가할 수 있습니다. 프로필은 자동 복원되지 않으며 서버 DB 백업을 대체하지 않습니다.
- 기존 로컬 H2 데이터는 아래 수동 절차로 계정에 연결한 뒤 JSON을 통해 온라인 계정에 가져올 수 있습니다. 실제 이관 전 별도 백업과 확인이 필요합니다.
- 실제 메일 발송, 실제 호스팅 환경, 동시 요청, 접근 통제 및 보안 검토를 마치기 전에는 실사용자를 받지 마세요. 무료 서비스는 사용량 제한과 중단 가능성이 있습니다.

## 기존 로컬 기록 가져오기

기존 로컬 데이터베이스와 같은 프로젝트에서 앱을 실행해 먼저 자신의 계정을 가입합니다. 앱을 종료한 후 `data` 폴더를 통째로 다른 위치에 복사해 백업하세요. 그리고 프로젝트 폴더의 터미널에서 아래 명령으로 본인이 가입한 이메일을 지정해 한 번 실행합니다.

```sh
./mvnw spring-boot:run -Dspring-boot.run.arguments="--keepit.legacy.import-email=본인이가입한이메일"
```

소유자 없는 기존 로컬 경험이 지정한 계정에 연결되며, 계정 프로필이 없으면 기존 프로필도 복사됩니다. 다른 계정에 이미 연결된 경험은 변경하지 않습니다. 계정이 없으면 변경 없이 실행이 실패합니다. 이전이 끝나면 앱을 종료하고 다음부터는 평소처럼 실행하세요. 이 기능은 운영 프로필에서는 비활성화됩니다.

## 암호화된 데이터베이스 백업

무료 시작을 위한 보조 수단으로 GitHub Actions 백업을 준비했습니다. 기본 브랜치에 반영하고 비밀 값을 설정해야 하루 한 번 작동합니다. 공개 저장소의 Actions 산출물은 다른 사람이 받을 수 있으므로 백업은 공개 인증서로 암호화하고, 복원용 개인 키는 GitHub에 올리지 않습니다. 각 파일은 30일 후 만료되며, 예약 실행 지연이나 비활성화가 가능하므로 주기적으로 성공 여부를 확인하고 별도 사본도 보관하세요. 개인 키를 잃으면 백업을 복구할 수 없습니다.

운영자가 개인 기기에서 복구용 키와 인증서를 만들고 안전하게 보관합니다. 실제 서비스용 키와 인증서는 소스 코드나 채팅에 올리지 마세요.

```sh
openssl req -x509 -newkey rsa:3072 -aes256 -keyout keepit-backup-private.pem -out keepit-backup-public.pem -subj '/CN=keepit-backup' -days 3650
chmod 600 keepit-backup-private.pem
```

GitHub 저장소의 Actions secrets에 `BACKUP_DATABASE_URL`(PostgreSQL 연결 URI), `BACKUP_CERT_PEM`(공개 인증서 내용)을 설정합니다. DB 제공자의 별도 CA 인증서가 필요하면 `BACKUP_DB_CA_PEM`도 설정합니다. 연결은 TLS 서버 인증을 검증하도록 설정되어 있습니다. 비밀번호와 개인 키를 출력하거나 커밋하지 마세요. 데이터베이스 URI에는 백업 대상 스키마에 필요한 읽기 권한만 가진 계정을 사용합니다.

복구 시험은 반드시 비어 있는 별도 DB에서 수행합니다. Actions에서 `.p7m` 파일을 내려받은 뒤 본인의 인증서와 개인 키로 복호화하고 `pg_restore`를 사용합니다. 실서비스 DB에 덮어쓰기 전에 대상과 백업 시점을 확인하세요.

```sh
openssl cms -decrypt -binary -inform DER -in keepit-backup.dump.p7m -recip keepit-backup-public.pem -inkey keepit-backup-private.pem -out keepit-restored.dump
pg_restore --no-owner --no-acl --exit-on-error --dbname="$RESTORE_DATABASE_URL" keepit-restored.dump
```
