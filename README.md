# keep it · 킵잇

**keep it(킵잇)**은 경험을 하나씩 기록하고 카드, 타임라인, 포트폴리오로 돌아보는 개인용 웹앱 시제품입니다.

## 구현한 기능

- 인턴/경력, 프로젝트, 공모전, 수상 경력, 자격증, 논문, 해외 경험, 동아리/대외활동, 교육 경험 등록
- 기본 종류가 맞지 않으면 `＋ 직접 추가`로 봉사활동·멘토링 등 분류 생성 및 재사용
- 제목, 기간, 기관, 장소, 함께한 사람, 역할, 내용, 성과, 태그, 관련 링크 기록
- 공모전·수상 경력의 최종 순위/진출 단계와 상 이름을 별도로 기록
- 관련 링크 최대 10개 추가·삭제 및 상세/포트폴리오에서 각각 새 탭으로 열기
- 경험 수정·삭제, 검색, 종류별 필터, 정렬, 카드·목록 보기
- 시작일 기준 가로 타임라인: 연도별 경험과 좌우 이동
- 별표로 선택한 경험의 포트폴리오 미리보기 및 브라우저 인쇄/PDF 저장
- 프로필 편집 및 H2 파일 DB 저장
- 첫 실행 시 가상 예시 6개 생성 및 예시만 삭제하는 기능

## 기술

Java 21 / Spring Boot 3.5.16 / Spring Data JPA / H2 / Maven Wrapper 3.9.11

화면은 Spring Boot가 제공하는 HTML, CSS, JavaScript로 작성했습니다.

## 실행

Java 21을 설치한 환경에서 프로젝트 폴더 안에서 실행합니다.

```sh
./mvnw spring-boot:run
```

Windows에서는 `mvnw.cmd spring-boot:run`을 사용합니다.

실행 후 <http://localhost:8087>에 접속합니다. 종료는 `Ctrl+C`입니다.

```sh
./mvnw test
./mvnw package
```

## IntelliJ IDEA

1. 프로젝트 폴더 또는 `pom.xml`을 Maven 프로젝트로 엽니다.
2. Project SDK는 Java 21, Maven은 Maven Wrapper를 사용합니다.
3. Maven 도구 창에서 프로젝트를 동기화합니다.
4. `MoaPortfolioApplication`의 `main` 또는 `keep it · 킵잇 실행` 구성을 실행합니다.
5. 테스트는 `PortfolioApiTests` 또는 `keep it · 전체 테스트` 구성을 사용합니다.

실행 포트가 사용 중이면 기존 앱을 종료하거나 `PORT` 환경변수를 변경합니다.

## 데이터 및 현재 범위

- DB는 프로젝트 폴더의 `data/`에 저장되며 Git에 포함하지 않습니다.
- 개인용 로컬 시제품으로, 회원가입·다중 사용자·공개 링크 배포·파일 업로드는 구현하지 않았습니다.
- 기본적으로 `127.0.0.1`에서만 접속할 수 있습니다.
- API 통합·기존 DB 회귀 테스트 18개를 포함하며 Maven 검증 및 GitHub Actions CI를 사용합니다.

경험 입력 흐름과 프로젝트 정리 방식은 [FlowCV](https://flowcv.com/)와 [Peerlist Projects](https://blog.peerlist.io/p/introducing-peerlist-projects)를 참고했습니다. 화면과 코드는 이 프로젝트에서 작성했습니다.

## 개발과 협업

- [협업 규칙](docs/CONTRIBUTING.md): 이슈 → 영역별 브랜치 → PR → Squash merge
- [경험 분류 설계와 조사 자료](docs/experience-categories.md)
- [브랜드·로고 시안](docs/brand/README.md)
- [기존 개발 이력과 BE/FE 구분](docs/development-log.md)

백엔드는 `src/main/java/`와 `src/test/java/`, 프론트엔드는 `src/main/resources/static/`에서 개발합니다. GitHub 이슈와 PR에 `be`, `fe`, `common` 영역 라벨과 작업 타입 라벨을 함께 붙입니다.


## 자기소개서 초안

자소서 초안 메뉴에서 지원 문항을 작성하고 참고할 경험을 선택하면, Gemini가 관련 경험과 초안을 제안합니다. 생성 버튼을 누르면 선택한 경험과 문항이 Google Gemini API로 전송됩니다. 결과는 화면에서 직접 수정하고 복사할 수 있으며 저장되지 않습니다. 초안의 사실관계는 직접 확인하세요.

사용하려면 별도로 발급한 Gemini API 키를 실행 환경의 `GEMINI_API_KEY`에 설정하고 서버를 다시 실행하세요. 모델은 기본 `gemini-3.1-flash-lite`이며 `GEMINI_MODEL`로 변경할 수 있습니다. 키를 Git이나 공유 파일에 기록하지 마세요. API 이용 한도와 요금은 Google 계정 및 모델 설정에 따릅니다. [Gemini API 안내](https://ai.google.dev/gemini-api/docs/text-generation)
