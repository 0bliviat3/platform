# my-platform (백엔드)

서비스마다 독립된 Spring Boot 앱을 한 저장소에서 같은 틀로 만드는 Gradle(Kotlin DSL) 멀티 모듈입니다.
지금은 공통 틀만 있고, 서비스는 `tools/new-domain.sh` 로 직접 만들어 가며 연습합니다.

- 기술: Java 21, Spring Boot 4.1, MyBatis, Spring Security
- 화면은 `../my-platform-web` 에 있습니다.

## 1. 구조

```
my-platform/
 ├ platform-kotlindsl/  빌드 공통 설정 (convention 플러그인). 모듈은 플러그인 id 한 줄로 공통 설정을 받는다
 ├ gradle/              버전 카탈로그 (libs.versions.toml). 라이브러리 버전은 여기서만 관리
 ├ platform-common/     프레임워크와 무관한 순수 Java 공통 (enum, 파일 유틸)   com.wan.common
 ├ platform-core/       Spring 공통 인프라 (응답, 예외, 보안, 로그, MyBatis)   com.wan.core
 ├ platform-<서비스>/   new-domain.sh 로 만드는 서비스 (실행 가능한 앱)       com.wan.<서비스>
 ├ templates/domain/    새 서비스의 원본 틀
 └ tools/new-domain.sh  템플릿을 복사해 서비스를 만드는 스크립트
```

- 의존 방향은 **서비스 → platform-core → platform-common** 한 방향입니다. 서비스끼리는 서로 의존하지 않습니다.
- `platform-` 로 시작하고 `build.gradle.kts` 가 있는 폴더는 자동으로 모듈에 포함됩니다. (`settings.gradle.kts` 를 고칠 필요가 없습니다)

### 빌드 공통 설정 (platform-kotlindsl)

| 플러그인 id                 | 쓰는 곳                   | 하는 일                                                   |
| --------------------------- | ------------------------- | --------------------------------------------------------- |
| `wan.java-conventions`     | 아래 두 개가 포함         | Java 21, UTF-8, Lombok, `-parameters`, JUnit              |
| `wan.library-conventions`  | platform-common, core     | jar 로 쓰이는 라이브러리 + Spring Boot BOM                |
| `wan.boot-app-conventions` | 서비스                    | 실행 가능한 앱 + platform-core 의존 + `<모듈명>.jar` 생성 |

## 2. platform-core 가 서비스에 주는 것

서비스는 core 를 의존성으로 추가하는 것만으로 아래를 받습니다. (`CoreAutoConfiguration`, 자동 구성)

| 기능        | 클래스                                         | 서비스에서 쓰는 법                                                         |
| ----------- | ---------------------------------------------- | -------------------------------------------------------------------------- |
| 공통 응답   | `CommonResponse`                               | `return CommonResponse.ok(data);` → `{ success, code, message, data }`     |
| 업무 예외   | `ErrorCode`, `BusinessException`               | 서비스별 `ErrorCode` enum 을 만들고 `throw new BusinessException(코드);`   |
| 예외 처리   | `GlobalExceptionHandler`                       | 직접 쓰지 않는다. 예외를 같은 JSON 오류 응답으로 바꿔 준다                 |
| 파일 응답   | `FileResponse`                                 | `return FileResponse.download(bytes, "이름.docx");`                        |
| 보안 기본값 | `SecurityConfiguration`                        | local 은 전부 허용, 그 외는 `/api/health` 빼고 인증 필요 (인증 수단은 없음) |
| 요청 로그   | `RequestLogInterceptor`                        | `/api/**` 호출마다 `→ 요청`, `← 상태 (ms)` 한 줄씩                         |
| 상태 확인   | `HealthController`                             | `GET /api/health`                                                          |
| MyBatis     | `MyBatisConfiguration`                         | `EMP_CODE` 컬럼 → `empCode` 자동 매핑                                      |

platform-common 의 파일 유틸(`SafePaths`, `StoredFileNames`, `SafeFileCopy`, `FileHashes`)은 파일 업로드를 다룰 때 씁니다.
사용자가 보낸 파일 이름을 저장 경로에 그대로 쓰지 않기 위한 도구입니다.

## 3. 새 서비스 만들기

```bash
tools/new-domain.sh <이름> --port <포트> [--db mariadb|postgresql] [--pkg <패키지명>]
tools/new-domain.sh memo --port 10510                    # -> platform-memo, com.wan.memo
```

- 포트는 직접 정합니다. 8080 같은 기본 포트는 피하고, 화면 앱 포트는 백엔드 포트 + 100 으로 맞추면 헷갈리지 않습니다. (예: 백엔드 10510, 화면 10610)
- 만들어지는 것: `Application.java`, `application.yml` / `-local.yml` / `-example.yml`, `Dockerfile`, 기동 테스트

### 서비스 안의 패키지 구성 (권장)

```
com.wan.<서비스>.<기능>/
 ├ controller/   요청을 받고 CommonResponse 로 감싸서 돌려준다. 로직은 두지 않는다
 ├ service/      업무 로직. 규칙 위반은 BusinessException 으로 알린다
 ├ dto/          요청·응답 모양 (Java record). 화면의 types.ts 와 필드 이름을 똑같이 맞춘다
 ├ enums/        <기능>ErrorCode (ErrorCode 구현), 상태값 enum
 ├ mapper/       MyBatis 매퍼 (XML 은 resources/mapper/ 아래)
 └ client/       외부 시스템 연동. 인터페이스를 두고 구현은 client/impl/ 에 둔다
```

API 경로는 `/api/<서비스>/<기능>` 으로 맞춥니다. (예: `/api/memo/notes`) 화면과 nginx 가 이 규칙에 기대고 있습니다.

## 4. 로컬 실행

JDK 21 이 필요합니다. 기본 `JAVA_HOME` 이 다른 버전이면 실행하는 창에서만 바꿉니다.

```powershell
$env:JAVA_HOME = "<JDK 21 경로>"
.\gradlew.bat :platform-memo:bootRun
```

- 프로파일을 지정하지 않으면 `local` 이 적용됩니다. DB 는 가짜 값이라 **DB 없이 기동**되고, DB 를 쓰는 기능만 실패합니다.
- 확인: `http://localhost:<포트>/api/health`
- IntelliJ: 저장소 루트(`my-platform`)를 열고 Project SDK 와 Gradle JVM 을 21 로 지정한 뒤 `Application.java` 의 `main` 을 실행합니다.
- 포트가 겹치면 `--server.port=<다른 포트>` 를 실행 인자로 넘깁니다.

## 5. 설정 규칙

| 파일                       | 내용                                                                      |
| -------------------------- | ------------------------------------------------------------------------- |
| `application.yml`          | 환경과 상관없이 한 벌. 실제 값 자리는 `${환경변수}` 로만 둔다             |
| `application-local.yml`    | 로컬용 가짜 값 (커밋한다)                                                 |
| `application-example.yml`  | 필요한 환경변수 **이름 목록** (값은 쓰지 않는다)                          |
| `.env` (서비스 폴더)       | 로컬에서 실제 서버에 붙을 때만 `이름=값` 으로 쓴다. 커밋하지 않는다      |

- 설정을 추가하면 `application.yml` 에 `${환경변수}` 로 쓰고 `application-example.yml` 의 목록에도 이름을 더합니다.
- 비밀값(주소, 계정, 키)은 저장소에 쓰지 않습니다.
- 외부 연동 설정은 `@ConfigurationProperties` record 로 받고, 값이 비었으면 시작할 때 막습니다.

## 6. 자주 쓰는 명령

```bash
./gradlew build                        # 전체 빌드 + 테스트
./gradlew :platform-memo:bootRun       # 로컬 실행
./gradlew :platform-memo:test          # 한 서비스만 테스트
./gradlew :platform-memo:bootJar       # 배포 jar -> platform-memo/build/libs/platform-memo.jar
```

들여쓰기는 탭입니다. (`.editorconfig`)
