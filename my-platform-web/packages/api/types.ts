/**
 * 백엔드 공통 응답 형식. (백엔드: com.wan.core.response.CommonResponse)
 * <p>success 로 성공과 실패를 구분한다. 성공이면 data 에 본문이, 실패이면 code 와 message 에 오류 내용이 담긴다.
 * 값이 없는 응답(백엔드 CommonResponse&lt;Void&gt;)은 data 가 null 이다.
 */
export type CommonResponse<T> = SuccessResponse<T> | FailResponse;

/** 성공 응답 */
export interface SuccessResponse<T> {
	success: true;
	code: "OK";
	message: null;
	data: T;
}

/** 실패 응답. code 는 백엔드 오류 코드(예: INVALID_FILE)이다. */
export interface FailResponse {
	success: false;
	code: string;
	message: string | null;
	data: null;
}

/** GET 조회 조건. 값이 null 이거나 undefined 이면 보내지 않는다. */
export type QueryParams = Record<string, string | number | boolean | null | undefined>;

/**
 * 파일을 올릴 때 보낼 항목. 항목 이름이 백엔드 @RequestParam 이름이다.
 * 같은 이름으로 여러 개를 보내려면 배열로 준다. 예) { file: file } 또는 { files: [a, b] }
 */
export type FormFields = Record<string, string | Blob | Array<string | Blob>>;

/** 내려받은 파일 */
export interface DownloadedFile {
	blob: Blob;
	/** 서버가 알려 준 파일 이름. 알 수 없으면 null */
	fileName: string | null;
}
