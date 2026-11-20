# platform (학습용)

그룹웨어에 붙는 작은 사내 서비스를 "서비스마다 백엔드 앱 + 화면 앱"으로 나눠 만드는 구조를 혼자 반복 구현해 보기 위한 저장소입니다.
원본 프로젝트에서 실제 서비스를 빼고 **공통 틀만** 남겼습니다.

```
platform/
 ├ my-platform/       백엔드: Spring Boot 멀티 모듈 (Java 21)     → my-platform/README.md
 └ my-platform-web/   프론트: Next.js 앱 모음 (npm workspaces)     → my-platform-web/README.md
```

## 전체 흐름

```
브라우저 ── /memo/...  ──▶ nginx ──▶ 화면 앱 (Next.js, apps/memo)          화면 틀
        └─ /api/memo/... ──▶ nginx ──▶ 백엔드 (Spring Boot, platform-memo)  JSON·파일
```

- 서비스 하나 = 백엔드 모듈 하나(`platform-<이름>`) + 화면 앱 하나(`apps/<이름>`). 서비스끼리는 서로 호출하지 않습니다.
- 브라우저는 항상 같은 도메인의 `/api` 로 호출합니다. 로컬에서는 nginx 대신 Next 개발 서버가 `/api` 를 백엔드로 넘깁니다.
- 공통 기능은 백엔드 `platform-core`, `platform-common` 과 프론트 `packages/api`, `packages/ui` 에 있습니다.
- 인증(SSO)과 배포(nginx, Docker, CI)는 이 틀에 아직 없습니다.

## 연습 한 바퀴

```bash
git switch -c round-01 baseline                                    # 1. 기준점에서 연습 브랜치 만들기

cd my-platform
tools/new-domain.sh memo --port 10510                              # 2. 백엔드 서비스 생성
#    ErrorCode → dto → service → controller 순서로 기능 구현
./gradlew :platform-memo:bootRun                                   #    http://localhost:10510/api/health

cd ../my-platform-web
tools/new-app.sh memo --title "메모" --port 10610 --api-port 10510  # 3. 화면 앱 생성
#    루트 package.json 에 "dev:memo" 추가 → npm install
#    types → api → hooks → components → app/page.tsx → layouts/menu.ts 순서로 구현
npm run dev:memo                                                   #    http://localhost:10610/memo

#    4. 개발자 도구 Network 탭에서 /api 요청과 응답 확인
git add -A && git commit -m "feat: 메모 기능"                      # 5. 기록 남기기
git push -u origin round-01
```

`main` 은 항상 기준 틀(`baseline`)로 두고, 연습은 바퀴마다 브랜치로 합니다.

## 처음 상태로 되돌리기

저장소 루트에서 실행합니다. 커밋하지 않은 변경과 새로 만든 파일이 지워집니다. (`node_modules`, `build` 처럼 무시 대상은 남습니다)

```bash
git switch main
git reset --hard baseline
git clean -fd
```

## 기록 보기

```bash
git log --oneline -- my-platform        # 백엔드만
git log --oneline -- my-platform-web    # 프론트만
```

커밋 메시지는 `feat(be): ...`, `feat(fe): ...`, 양쪽을 함께 바꾸면 `feat: ...` 으로 구분합니다.
