# 배포 준비 (미완료)

실제 배포 전에 PostgreSQL, SMTP 발송 계정과 HTTPS 주소가 필요합니다. 이 문서는 예시 설정이며 운영 준비 완료를 의미하지 않습니다.

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
KEEPIT_SMTP_HOST=...
KEEPIT_SMTP_PORT=587
KEEPIT_SMTP_USER=...
KEEPIT_SMTP_PASSWORD=...
KEEPIT_MAIL_FROM=...
KEEPIT_PUBLIC_URL=https://실제-사이트-주소
GEMINI_API_KEY=...
```

운영 프로필은 생성된 테이블을 검증만 하며, 운영 서버는 HTTPS를 제공하는 신뢰할 수 있는 프록시 뒤에 둡니다. 메일 발송 기능은 SMTP 인증과 STARTTLS를 사용하며, 발신 주소의 소유권 인증과 실제 이메일 송수신 검증이 필요합니다.

## 기능과 남은 작업

- 이메일 인증 및 비밀번호 재설정 링크는 30분 동안 한 번만 쓸 수 있습니다. 비밀번호를 변경하면 기존 로그인 세션이 무효화됩니다.
- 로그인 및 AI 요청 횟수 제한은 **단일 프로세스의 임시 제한**입니다. 서버 재시작 시 초기화되고 여러 서버 사이에서 공유되지 않습니다. 공개 전 신뢰할 수 있는 프록시의 IP 처리와 별도의 공유 제한 장치를 검증해야 합니다.
- 사용자는 `내 기록 백업`을 눌러 경험과 프로필을 JSON으로 내려받을 수 있습니다. 현재 자동 복원 기능은 없으며 서버 DB 백업을 대체하지 않습니다.
- 기존 로컬 H2 데이터의 계정 연결/이전 기능과 DB 자동 백업은 구현되지 않았습니다. 실제 데이터의 이관은 별도 백업과 확인이 필요합니다.
- 실제 SMTP, 실제 호스팅 환경, 동시 요청, 접근 통제 및 보안 검토를 마치기 전에는 실사용자를 받지 마세요. 무료 서비스는 사용량 제한과 중단 가능성이 있습니다.
