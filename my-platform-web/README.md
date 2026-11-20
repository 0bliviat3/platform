# my-platform-web (프론트)

서비스마다 독립된 Next.js 앱을 한 저장소(npm workspaces)에서 같은 틀로 만드는 프론트 저장소입니다.
지금은 공통 패키지와 템플릿만 있고, 화면 앱은 `tools/new-app.sh` 로 직접 만들어 가며 연습합니다.

- 기술: Next.js 16 (App Router), React 19, TypeScript(strict), Tailwind CSS 4, ESLint, Prettier
- 백엔드는 `../my-platform` 에 있습니다. 화면은 백엔드 API 를 호출하므로 화면을 확인하려면 백엔드도 함께 실행합니다.

## 1. 구조

```
my-platform-web/
 ├ packages/
 │   ├ api/            백엔드 API 호출 공통 도구 (@wan/api). 모든 앱이 쓴다
 │   └ ui/             공통 UI 컴포넌트와 디자인 토큰 (@wan/ui). 모든 앱이 쓴다
 ├ apps/               new-app.sh 로 만드는 화면 앱. 앱끼리는 서로 독립 (따로 빌드·배포)
 ├ templates/app/      새 앱의 원본 틀
 └ tools/new-app.sh    템플릿을 복사해 앱을 만드는 스크립트
```

루트에서 `npm install` 한 번이면 모든 앱과 패키지가 설치되고 서로 연결됩니다.

## 2. 사전 준비

| 도구    | 버전                | 확인 명령       |
| ------- | ------------------- | --------------- |
| Node.js | 20.9 이상 (24 권장) | `node -v`       |
| JDK     | 21 (백엔드 실행용)  | `java -version` |

## 3. 새 화면 앱 만들기

백엔드 서비스를 먼저 만들고(`../my-platform/tools/new-domain.sh`), 같은 이름으로 화면 앱을 만듭니다.

```bash
tools/new-app.sh memo --title "메모" --port 10610 --api-port 10510
```

1. 루트 `package.json` 의 `scripts` 에 `"dev:memo": "npm run dev -w memo"` 를 추가합니다.
2. 루트에서 `npm install` 을 실행합니다. (새 앱을 워크스페이스에 연결)
3. 백엔드를 실행합니다: `../my-platform` 에서 `./gradlew :platform-memo:bootRun`
4. 화면을 실행합니다: `npm run dev:memo` → **http://localhost:10610/memo**

주소 끝의 `/memo` 가 필요합니다. 서버에서는 nginx 가 경로로 앱을 나누기 때문에 앱도 그 경로 아래에서 동작하도록 맞춰 두었습니다. (`next.config.ts` 의 `basePath`)

## 4. 화면이 백엔드와 연결되는 방식

```
브라우저 ──▶ /memo/...   화면 요청 ──▶ Next.js (화면 앱)      : 데이터 없는 틀(HTML + JS)
        └──▶ /api/...    API 요청  ──▶ 백엔드 (Spring Boot)   : JSON 또는 파일
```

- 브라우저는 **항상 같은 도메인의 `/api/...`** 로 백엔드를 호출합니다. 백엔드 주소를 코드에 쓰지 않습니다.
- 서버에서는 nginx 가 `/api` 를 백엔드로, `/<앱이름>` 을 화면 앱으로 보냅니다.
- 로컬에는 nginx 가 없으므로 Next 개발 서버가 `/api` 요청을 백엔드로 넘겨줍니다. (`next.config.ts` 의 `rewrites`, `next dev` 에서만 동작)
- 백엔드 주소를 바꾸려면 앱 폴더의 `.env.example` 을 `.env.local` 로 복사해 `API_BASE_URL` 을 고칩니다.

데이터를 그리는 순서는 다음과 같습니다.

1. 틀 HTML 이 먼저 보입니다.
2. JS 가 로드되면 React 가 붙습니다(hydration).
3. 각 화면 컴포넌트의 훅(`useLoad` 의 `useEffect`)이 `/api` 를 호출합니다.
4. 응답이 온 영역부터 다시 그립니다.

개발 모드(`next dev`)에서는 React StrictMode 때문에 화면을 열 때 요청이 두 번 나가는 것이 정상입니다.

## 5. 앱 안의 폴더 구조

```
apps/<앱>/
 ├ app/                 라우트(주소)만 둔다. page.tsx 는 얇게: 제목 + 기능의 화면 컴포넌트 조립
 ├ features/<기능>/     기능별 코드. 한 기능의 코드는 한 폴더에 모은다
 │   ├ types.ts           백엔드 응답 모양 (백엔드 DTO 와 필드 이름 같게)
 │   ├ api/               백엔드 호출 함수 (API 하나 = 함수 하나)
 │   ├ hooks/             상태·호출 로직 (useXxx)
 │   ├ utils/             순수 계산 함수 (화면과 무관)
 │   └ components/        화면 부품. 그리기만 하고 로직은 hooks/utils 에 맡긴다
 ├ layouts/             틀: AppShell(상단바), SideNav(왼쪽 메뉴), PageHeader, menu.ts(메뉴 정의)
 ├ shared/              기능과 무관한 공통 (useLoad, LoadView 등)
 └ next.config.ts       basePath, 개발용 /api 전달
```

새 기능은 이 순서로 파일을 만듭니다.

| 하려는 일              | 두는 곳                                                 |
| ---------------------- | ------------------------------------------------------- |
| 응답 타입              | `features/<기능>/types.ts`                              |
| 백엔드 API 호출        | `features/<기능>/api/`                                  |
| 호출 결과·입력 상태    | `features/<기능>/hooks/`                                |
| 계산·변환              | `features/<기능>/utils/`                                |
| 화면 조각              | `features/<기능>/components/`                           |
| 새 주소(화면)          | `app/<주소>/page.tsx` + `layouts/menu.ts` 에 메뉴 한 줄 |
| 두 기능 이상이 쓰는 것 | `shared/` (앱 안) 또는 `packages/ui` (앱 간)            |

의존 방향은 `app → features → shared` 한 방향입니다. 여러 기능을 조립하는 화면(예: 대시보드)만 다른 기능의 hooks/components 를 가져다 씁니다.

## 6. API 호출과 타입 규칙

호출은 `@wan/api` 의 `api` 로만 합니다. `fetch` 를 직접 쓰지 않습니다.

```ts
import { api } from "@wan/api";

/** 백엔드: GET /api/memo/notes */
export function getNotes(): Promise<Note[]> {
  return api.get<Note[]>("/api/memo/notes");
}

api.get<Page>("/api/memo/notes", { keyword: "회의", page: 1 }); // 조회 조건은 자동 인코딩
api.post<Note>("/api/memo/notes", { title, body }); // 등록 (put, delete 도 같은 모양)
api.upload<UploadResult>("/api/memo/files", { files: [a, b] }); // 파일을 올리고 JSON 받기
api.download("/api/memo/files/export", { file }); // 파일을 올리고(또는 GET) 파일 받기
```

- 응답은 공통 형식 `{ success, code, message, data }` 에서 `data` 만 꺼내 돌려줍니다.
- 실패하면 항상 `ApiError` 가 던져집니다. 화면에는 `getErrorMessage(e)` 로 문구를 꺼내 보여 줍니다.
- 연결 실패, 시간 초과(일반 30초, 파일 5분), 상태별 기본 문구는 공통 도구가 처리합니다.
- 조회 조건(`params`)은 주소에 남아 로그에 기록됩니다. 개인정보나 비밀값은 `post` 본문으로 보냅니다.
- 받은 파일은 `saveFile(file, "기본이름")` 으로 저장합니다.
- 401 처리 자리는 `packages/api/client.ts` 의 `handleUnauthorized` 한 곳입니다. (인증은 아직 없음)

타입은 백엔드 DTO(Java record) 하나에 프론트 타입 하나를 둡니다. 필드 이름과 타입 이름을 백엔드와 같게 쓰고, 주석에 대응하는 백엔드 클래스를 적습니다.
`null` 이 올 수 있는 필드는 `필드: 타입 | null`, 값이 없는 응답은 `api.post<void>(...)` 로 받습니다.

## 7. 코드 규칙

- 스타일은 Tailwind 클래스로 씁니다. 색상·글꼴은 토큰 이름(`bg-primary`, `text-ink-muted` 등)을 쓰고, 토큰은 `packages/ui/styles/tokens.css` 에서만 바꿉니다.
- `packages/ui` 는 업무 개념(메모, 카드 등)을 모릅니다. 업무가 들어가면 앱으로 옮깁니다.
- 두 곳 이상에서 쓸 때만 공통(`shared`, `packages/ui`)으로 올립니다.
- 들여쓰기는 탭(폭 4), 한 줄 최대 120자. JSON, YAML, Markdown 은 스페이스 2칸입니다.
- 비밀값을 `NEXT_PUBLIC_` 환경변수에 넣지 않습니다. 브라우저에 그대로 노출됩니다.
- Next.js 16 은 이전 버전과 달라진 부분이 있습니다. 막히면 설치된 문서(`node_modules/next/dist/docs/`)를 먼저 확인합니다.

## 8. 자주 쓰는 명령

루트(`my-platform-web`)에서 실행합니다.

| 명령                   | 설명                              |
| ---------------------- | --------------------------------- |
| `npm install`          | 전체 의존성 설치 (앱 추가 후에도) |
| `npm run dev:<앱>`     | 앱 개발 서버                      |
| `npm run build`        | 모든 앱 프로덕션 빌드             |
| `npm run lint`         | ESLint 검사                       |
| `npm run format`       | Prettier 로 포맷 정리             |
| `npm run format:check` | 포맷 검사 (수정하지 않음)         |

## 9. 문제 해결

| 증상                                   | 원인과 해결                                                                                  |
| -------------------------------------- | -------------------------------------------------------------------------------------------- |
| 화면에 "서버에 연결할 수 없습니다."    | 백엔드가 꺼져 있습니다. `http://localhost:<백엔드포트>/api/health` 가 열리는지 확인합니다.   |
| `localhost:<포트>` 만 열면 화면이 없음 | 주소 끝에 `/<앱이름>` 을 붙입니다.                                                           |
| `npm run start` 에서 API 가 실패       | 정상입니다. `/api` 전달은 `next dev` 에서만 동작합니다. 화면 확인은 `npm run dev:<앱>` 으로. |
| 새 앱의 `@wan/ui` 를 못 찾음           | 루트에서 `npm install` 을 다시 실행합니다.                                                   |
| 포맷 검사 실패                         | `npm run format` 으로 정리합니다.                                                            |
