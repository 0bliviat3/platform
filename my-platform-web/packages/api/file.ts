import type { DownloadedFile } from "./types";

/**
 * Content-Disposition 헤더에서 파일 이름을 꺼낸다.
 *
 * @param header 응답 헤더 값
 * @returns 파일 이름. 없거나 해석할 수 없으면 null
 */
export function parseFileName(header: string | null): string | null {
	if (!header) return null;
	const encoded = /filename\*=UTF-8''([^;]+)/i.exec(header);
	if (encoded) {
		try {
			return decodeURIComponent(encoded[1]);
		} catch {
			return null;
		}
	}
	const plain = /filename="([^"]*)"/i.exec(header);
	return plain ? plain[1] : null;
}

/**
 * 받은 파일을 브라우저 다운로드로 저장한다. (브라우저에서만 쓴다)
 *
 * @param file         api.download 로 받은 파일
 * @param fallbackName 서버가 이름을 주지 않았을 때 쓸 이름
 * @returns 실제로 저장한 파일 이름
 */
export function saveFile(file: DownloadedFile, fallbackName = "download"): string {
	const name = file.fileName ?? fallbackName;
	const url = URL.createObjectURL(file.blob);
	const link = document.createElement("a");
	link.href = url;
	link.download = name;
	document.body.appendChild(link);
	link.click();
	link.remove();
	// 다운로드가 시작된 뒤에 주소를 해제한다.
	setTimeout(() => URL.revokeObjectURL(url), 0);
	return name;
}
