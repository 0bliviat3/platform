/**
 * API 호출 실패.
 * <p>백엔드가 오류 응답을 준 경우 code, message 는 백엔드 값이다. 서버에 연결하지 못했으면 code 는 NETWORK_ERROR,
 * 응답이 너무 늦으면 TIMEOUT 이고 status 는 0 이다.
 */
export class ApiError extends Error {
	readonly code: string;
	readonly status: number;

	constructor(code: string, message: string, status: number) {
		super(message);
		this.name = "ApiError";
		this.code = code;
		this.status = status;
	}

	/** 로그인이 안 됐거나 만료됨 */
	get isUnauthorized(): boolean {
		return this.status === 401;
	}

	/** 서버에 닿지 못함 (연결 실패 또는 응답 시간 초과) */
	get isNetworkError(): boolean {
		return this.code === "NETWORK_ERROR" || this.code === "TIMEOUT";
	}
}

/** 서버가 JSON 오류 본문을 주지 못했을 때(프록시, 게이트웨이가 직접 돌려주는 오류 등) 보여 줄 문구 */
const STATUS_MESSAGES: Record<number, string> = {
	400: "잘못된 요청입니다.",
	401: "로그인이 필요하거나 로그인이 만료되었습니다.",
	403: "권한이 없습니다.",
	404: "요청한 대상을 찾을 수 없습니다.",
	413: "파일이 너무 큽니다.",
	415: "지원하지 않는 형식입니다.",
	429: "요청이 너무 많습니다. 잠시 후 다시 시도해 주세요.",
	500: "서버 오류가 발생했습니다.",
	502: "서버가 응답하지 않습니다. 잠시 후 다시 시도해 주세요.",
	503: "서버가 응답하지 않습니다. 잠시 후 다시 시도해 주세요.",
	504: "서버가 응답하지 않습니다. 잠시 후 다시 시도해 주세요.",
};

/**
 * HTTP 상태 코드에 맞는 기본 오류 문구.
 *
 * @param status HTTP 상태 코드
 * @returns 정해진 문구가 없으면 일반 문구
 */
export function defaultMessage(status: number): string {
	return STATUS_MESSAGES[status] ?? "요청을 처리하지 못했습니다.";
}

/**
 * 화면에 보여 줄 오류 메시지를 꺼낸다. 화면마다 따로 판단하지 않고 항상 이 함수를 쓴다.
 * <p>ApiError 이면 백엔드가 준 메시지(또는 상태별 기본 문구)를, 그 외 예상하지 못한 오류이면 일반 문구를 돌려준다.
 *
 * @param error catch 로 잡은 값
 * @returns 사용자에게 보여 줄 메시지
 */
export function getErrorMessage(error: unknown): string {
	return error instanceof ApiError ? error.message : "알 수 없는 오류가 발생했습니다.";
}
