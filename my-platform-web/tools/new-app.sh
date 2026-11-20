#!/usr/bin/env bash
# 새 화면 앱(apps/<이름>)을 템플릿에서 만든다.
# 사용법: tools/new-app.sh <이름> --title <표시명> --port <프론트포트> --api-port <백엔드포트>
#   이름: 소문자 영문/숫자 (앱 폴더는 apps/<이름>, 접속 주소는 /<이름>)
#   예) tools/new-app.sh aegis --title "WAN Aegis 보안센터" --port 10603 --api-port 10503
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
NAME="${1:-}"
[[ -n "$NAME" ]] || { sed -n '2,6p' "$0"; exit 1; }
shift

TITLE=""
PORT=""
API_PORT=""
while [[ $# -gt 0 ]]; do
	case "$1" in
		--title) TITLE="$2"; shift 2 ;;
		--port) PORT="$2"; shift 2 ;;
		--api-port) API_PORT="$2"; shift 2 ;;
		*) echo "알 수 없는 옵션: $1" >&2; exit 1 ;;
	esac
done

[[ "$NAME" =~ ^[a-z][a-z0-9]*$ ]] || { echo "이름은 소문자 영문/숫자만 가능합니다: $NAME" >&2; exit 1; }
[[ -n "$TITLE" ]] || { echo "--title 이 필요합니다." >&2; exit 1; }
[[ "$TITLE" != *'|'* && "$TITLE" != *'&'* && "$TITLE" != *'\'* ]] || { echo "--title 에는 | & \\ 를 쓸 수 없습니다." >&2; exit 1; }
# 기본 포트(3000, 8080 등)를 실수로 쓰지 않도록 반드시 직접 정하게 한다.
[[ "$PORT" =~ ^[0-9]{4,5}$ ]] || { echo "--port 가 필요합니다. (4~5자리 숫자)" >&2; exit 1; }
[[ "$API_PORT" =~ ^[0-9]{4,5}$ ]] || { echo "--api-port 가 필요합니다. (4~5자리 숫자)" >&2; exit 1; }

DEST="$ROOT/apps/$NAME"
[[ ! -e "$DEST" ]] || { echo "이미 존재합니다: $DEST" >&2; exit 1; }

cp -r "$ROOT/templates/app" "$DEST"
mv "$DEST/package.json.tpl" "$DEST/package.json"

# 플레이스홀더 치환
find "$DEST" -type f ! -name '*.ico' | while read -r f; do
	sed -i \
		-e "s|__NAME__|$NAME|g" \
		-e "s|__TITLE__|$TITLE|g" \
		-e "s|__PORT__|$PORT|g" \
		-e "s|__API_PORT__|$API_PORT|g" \
		"$f"
done

echo "생성 완료: $NAME (표시명 $TITLE, 프론트 $PORT, 백엔드 $API_PORT)"
echo "  다음 할 일 1) 루트 package.json 의 scripts 에 \"dev:$NAME\": \"npm run dev -w $NAME\" 한 줄 추가"
echo "  다음 할 일 2) 저장소 루트에서 npm install 실행 (워크스페이스 연결)"
echo "  실행: npm run dev:$NAME  →  http://localhost:$PORT/$NAME"
