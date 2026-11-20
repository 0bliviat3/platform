# __TITLE__ (`__NAME__`)

`tools/new-app.sh` 로 만든 화면 앱입니다. 폴더 구조와 규칙은 저장소 루트 `README.md` 를 따릅니다.

- 개발 서버: `npm run dev:__NAME__` → http://localhost:__PORT__/__NAME__
- 백엔드(`platform-__NAME__`): http://localhost:__API_PORT__ (개발 서버가 `/api` 를 대신 넘겨준다)
- 화면을 추가할 때: `features/<기능>/` 아래에 types, api, hooks, components 를 만들고, `app/<경로>/page.tsx` 에서 불러온다. 메뉴는 `layouts/menu.ts` 에 한 줄 더한다.
