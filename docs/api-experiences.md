# 경험 API

경험 CRUD는 `/api/experiences`를 사용합니다. `POST`는 201과 생성 결과, `GET`은 목록/단일 결과, `PUT /{id}`는 수정 결과, `DELETE /{id}`는 204를 반환합니다.

## 인턴/경력

```json
{
  "type": "WORK",
  "title": "백엔드 개발 인턴",
  "organization": "회사 / 개발팀",
  "startDate": "2026-07-01",
  "ongoing": true,
  "role": "백엔드 인턴",
  "description": "담당 업무와 직접 기여한 내용",
  "outcome": "업무 성과와 배운 점",
  "tags": "Java, SQL",
  "featured": true
}
```

## 직접 지정 분류

`type: CUSTOM`이면 `customCategory`에 최대 30자의 이름을 입력합니다. 누락·공백·30자 초과는 400입니다. 앞뒤·연속 공백은 저장 시 정리하며, 기본 종류로 수정하면 이름을 비웁니다.

```json
{
  "type": "CUSTOM",
  "customCategory": "봉사활동",
  "title": "청소년 코딩 교육",
  "startDate": "2026-05-01",
  "featured": true
}
```

응답은 `id`, 입력 필드, `demo`, `customCategory`를 포함합니다. 기존 8종의 요청은 새 필드 없이도 동작합니다. 제목·시작일·종류는 필수이고, 종료일은 시작일 이전일 수 없습니다. 진행 중이면 종료일을 비웁니다.

개인용 로컬 API이며 로그인·사용자별 권한은 아직 없습니다.

## 기존 H2 파일 DB 호환

분류는 Java enum을 문자열(VARCHAR)로 명시해 저장합니다. 초기 H2 네이티브 ENUM에 고정된 8종 제한을 없애고 `ddl-auto=update`로 기존 행을 보존한 채 전환합니다. 초기 ENUM 스키마와 기존 행이 있는 상태에서 WORK/CUSTOM 저장 및 이전 행 유지를 회귀 테스트합니다.

## 최종 결과와 관련 링크

- `challenge`: 어려움과 해결(선택, 5000자). AI 초안에 참고할 수 있습니다.
- `credentialNumber`: 자격번호(선택, 160자). AI 초안으로 전송하지 않습니다.
- 기존 요청에서 두 필드를 생략하면 기존 값을 유지하고, 빈 문자열을 보내면 값을 비웁니다.
- `placement`: 공모전/수상 경력의 최종 순위 또는 진출 단계(40자). 예: `3위`, `본선 진출`, `상위 10%`.
- `awardName`: 수상명(100자). 예: `대상`, `우수상`. 수상하지 않았거나 미정이면 비워도 됩니다.
- `outcome`: 기존 성과·배운 점 필드는 별도로 유지합니다. 다른 종류로 변경하면 순위·수상명은 비웁니다.
- `links`: HTTP(S) URL 배열, 최대 10개, 각 1,000자. 앞뒤 공백과 같은 URL 중복을 제거하고 입력 순서를 유지합니다.
- `links`가 생략되면 기존 `link` 한 개를 사용합니다. 기존 파일 DB의 단일 링크도 응답의 `links` 배열에 포함합니다.
- `links: []`는 모든 링크 삭제를 뜻하며, 이때 기존 `link`도 비웁니다. `link`와 `links`를 함께 보내면 `links`가 우선합니다.
- 응답의 기존 `link`는 첫 링크를 유지하여 이전 클라이언트와 호환합니다. 경험 삭제 시 연결된 링크도 삭제합니다.

```json
{
  "type": "COMPETITION",
  "title": "캠퍼스 해커톤",
  "startDate": "2026-10-23",
  "placement": "3위",
  "awardName": "우수상",
  "links": ["https://example.com/code", "https://example.com/slides"]
}
```
