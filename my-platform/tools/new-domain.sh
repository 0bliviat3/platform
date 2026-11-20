#!/usr/bin/env bash
# 새 서비스 모듈(platform-<이름>)을 템플릿에서 만든다.
# 사용법: tools/new-domain.sh <이름> --port <포트> [--db mariadb|postgresql] [--pkg <패키지명>]
#   이름: 소문자 영문/숫자 (모듈 폴더는 platform-<이름>, 기본 패키지는 com.wan.<이름>)
#   포트: 기본 포트(8080 등)를 쓰지 않도록 반드시 직접 정한다. (Aegis 10503 부터 1씩 증가)
#   예) tools/new-domain.sh aegis --port 10503
#       tools/new-domain.sh webot --port 10504 --db postgresql
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
NAME="${1:-}"
[[ -n "$NAME" ]] || { sed -n '2,8p' "$0"; exit 1; }
shift

DB="mariadb"
PKG="$NAME"
PORT=""
while [[ $# -gt 0 ]]; do
	case "$1" in
		--db) DB="$2"; shift 2 ;;
		--pkg) PKG="$2"; shift 2 ;;
		--port) PORT="$2"; shift 2 ;;
		*) echo "알 수 없는 옵션: $1" >&2; exit 1 ;;
	esac
done

[[ "$NAME" =~ ^[a-z][a-z0-9]*$ ]] || { echo "이름은 소문자 영문/숫자만 가능합니다: $NAME" >&2; exit 1; }
[[ "$PKG" =~ ^[a-z][a-z0-9]*$ ]] || { echo "패키지명은 소문자 영문/숫자만 가능합니다: $PKG" >&2; exit 1; }
[[ "$PORT" =~ ^[0-9]{4,5}$ ]] || { echo "--port 가 필요합니다. (4~5자리 숫자, 기본 포트 8080 은 쓰지 않는다)" >&2; exit 1; }
NAME_UPPER="${NAME^^}" # 환경변수 이름에 쓴다. (예: aegis -> AEGIS_DB_URL)
case "$DB" in
	mariadb)
		DB_DRIVER='libs.mariadb.jdbc'
		DB_URL="jdbc:mariadb://localhost:3306/$NAME" ;;
	postgresql)
		DB_DRIVER='"org.postgresql:postgresql"'
		DB_URL="jdbc:postgresql://localhost:5432/$NAME" ;;
	*) echo "--db 는 mariadb 또는 postgresql 이어야 합니다: $DB" >&2; exit 1 ;;
esac

DEST="$ROOT/platform-$NAME"
[[ ! -e "$DEST" ]] || { echo "이미 존재합니다: $DEST" >&2; exit 1; }

cp -r "$ROOT/templates/domain" "$DEST"
mv "$DEST/build.gradle.kts.tpl" "$DEST/build.gradle.kts"
for d in main test; do
	mv "$DEST/src/$d/java/com/wan/__PKG__" "$DEST/src/$d/java/com/wan/$PKG"
done

# 플레이스홀더 치환
find "$DEST" -type f | while read -r f; do
	sed -i \
		-e "s|__NAME_UPPER__|$NAME_UPPER|g" \
		-e "s|__NAME__|$NAME|g" \
		-e "s|__PKG__|$PKG|g" \
		-e "s|__PORT__|$PORT|g" \
		-e "s|__DB_DRIVER__|$DB_DRIVER|g" \
		-e "s|__DB_URL__|$DB_URL|g" \
		"$f"
done

echo "생성 완료: $NAME (패키지 com.wan.$PKG, DB $DB, 포트 $PORT)"
echo "  실행: ./gradlew :platform-$NAME:bootRun"
echo "  배포 jar: ./gradlew :platform-$NAME:bootJar"
