/*
 * 백엔드 API 호출 도구의 본체. 앱은 이 파일의 함수를 직접 쓰지 않고, 맨 아래의 `api` 로만 호출한다.
 *
 * 위에서 아래로 한 겹씩 쌓여 있다.
 *   1) 부품        buildUrl, toFormData                    주소(조회 조건 붙이기)와 파일 폼 데이터 만들기
 *   2) 전송        send                                    fetch 한 번. 연결 실패와 시간 초과를 ApiError 로 바꾼다
 *   3) 응답 해석   toApiError, readData                    오류 응답과 공통 응답을 해석해서 data 를 꺼낸다
 *   4) 요청 종류   requestJson, requestUpload, requestDownload
 *                  JSON 주고받기 / 파일 올리고 JSON 받기 / 파일 받기. 보내는 방식과 제한 시간만 다르다
 *   5) 공개        api.get, post, put, delete, upload, download
 *
 * 호출 흐름 (예: api.post)
 *   api.post → requestJson → send(fetch) → readData → 성공이면 data 를 돌려주고, 실패면 ApiError 를 던진다
 *
 * 지키는 규칙
 *   - 실패는 항상 ApiError 로 던진다. (서버 오류 응답, 연결 실패, 시간 초과, 응답 형식 이상 모두)
 *   - 파일 받기(requestDownload)는 성공 응답이 JSON 이 아니라 파일이므로 readData 를 거치지 않는다.
 *     오류일 때만 JSON 오류 본문을 해석한다.
 *   - 401 은 handleUnauthorized 한 곳에서만 처리한다.
 */
import { ApiError, defaultMessage } from "./errors";
import { parseFileName } from "./file";
import type { CommonResponse, DownloadedFile, FailResponse, FormFields, QueryParams } from "./types";

/** 일반 요청의 응답 대기 제한 (30초) */
const DEFAULT_TIMEOUT_MS = 30 * 1000;
/** 파일을 올리거나 받는 요청의 응답 대기 제한 (5분) */
const FILE_TIMEOUT_MS = 5 * 60 * 1000;

/**
 * 401(SSO 만료 등) 처리 자리.
 * <p>처리 방식(로그인 화면 이동, SSO 재인증)은 백엔드 인증 구성이 확정된 뒤 정한다. [미확정: 02 규칙 7.3]
 * 화면마다 따로 처리하지 않고 이 함수 한 곳에서 처리한다.
 */
function handleUnauthorized(): void {
	// 아직 정해진 동작이 없다.
}

/** 경로 뒤에 조회 조건을 붙인다. 값은 자동으로 인코딩된다. */
function buildUrl(path: string, params?: QueryParams): string {
	if (!params) return path;
	const query = new URLSearchParams();
	for (const [key, value] of Object.entries(params)) {
		if (value !== null && value !== undefined) query.append(key, String(value));
	}
	const text = query.toString();
	if (!text) return path;
	return `${path}${path.includes("?") ? "&" : "?"}${text}`;
}

function toFormData(fields: FormFields): FormData {
	const form = new FormData();
	for (const [name, value] of Object.entries(fields)) {
		for (const item of Array.isArray(value) ? value : [value]) {
			form.append(name, item);
		}
	}
	return form;
}

/** 요청을 보낸다. 연결 실패와 시간 초과를 ApiError 로 바꾼다. */
async function send(path: string, init: RequestInit, timeoutMs: number): Promise<Response> {
	try {
		return await fetch(path, { ...init, signal: AbortSignal.timeout(timeoutMs) });
	} catch (e) {
		if (e instanceof DOMException && e.name === "TimeoutError") {
			throw new ApiError("TIMEOUT", "서버 응답이 너무 늦어 요청을 중단했습니다.", 0);
		}
		throw new ApiError("NETWORK_ERROR", "서버에 연결할 수 없습니다.", 0);
	}
}

/** 오류 응답을 ApiError 로 바꾼다. JSON 본문이 없으면 상태 코드별 기본 문구를 쓴다. */
async function toApiError(response: Response): Promise<ApiError> {
	let body: Partial<FailResponse> | null = null;
	try {
		body = (await response.json()) as Partial<FailResponse>;
	} catch {
		// 본문이 JSON 이 아니면(프록시 오류 등) 기본 문구를 쓴다.
	}
	return new ApiError(
		body?.code ?? "UNKNOWN_ERROR",
		body?.message ?? defaultMessage(response.status),
		response.status,
	);
}

/** 공통 응답에서 data 를 꺼낸다. 실패하면 ApiError 를 던진다. */
async function readData<T>(response: Response): Promise<T> {
	if (response.status === 401) handleUnauthorized();
	if (!response.ok) throw await toApiError(response);

	let body: CommonResponse<T> | null = null;
	try {
		body = (await response.json()) as CommonResponse<T>;
	} catch {
		// 본문이 JSON 이 아니면 아래에서 처리한다.
	}
	if (!body || !body.success) {
		throw new ApiError(
			body?.code ?? "UNKNOWN_ERROR",
			body?.message ?? "응답 형식이 올바르지 않습니다.",
			response.status,
		);
	}
	return body.data;
}

async function requestJson<T>(method: string, path: string, body?: unknown): Promise<T> {
	const response = await send(
		path,
		{
			method,
			headers: body === undefined ? undefined : { "Content-Type": "application/json" },
			body: body === undefined ? undefined : JSON.stringify(body),
		},
		DEFAULT_TIMEOUT_MS,
	);
	return readData<T>(response);
}

async function requestUpload<T>(path: string, fields: FormFields): Promise<T> {
	// Content-Type 을 지정하지 않는다. 브라우저가 multipart 경계값을 붙여 준다.
	const response = await send(path, { method: "POST", body: toFormData(fields) }, FILE_TIMEOUT_MS);
	return readData<T>(response);
}

async function requestDownload(path: string, fields?: FormFields): Promise<DownloadedFile> {
	const init: RequestInit = fields ? { method: "POST", body: toFormData(fields) } : { method: "GET" };
	const response = await send(path, init, FILE_TIMEOUT_MS);
	if (response.status === 401) handleUnauthorized();
	if (!response.ok) throw await toApiError(response);
	return {
		blob: await response.blob(),
		fileName: parseFileName(response.headers.get("Content-Disposition")),
	};
}

/**
 * 백엔드 API 호출 도구. 모든 API 호출은 이 객체를 거친다.
 * <p>경로는 도메인 루트 기준(/api/...)으로 쓴다. 로컬에서는 Next 가, 서버에서는 Nginx 가 백엔드로 전달한다.
 * <p>실패하면 항상 {@link ApiError} 를 던진다. 화면에는 {@link getErrorMessage} 로 문구를 꺼내 보여 준다.
 * <p>조회 조건(get 의 params)은 주소에 남아 접근 로그에 기록된다. 개인정보나 비밀값은 params 가 아니라 post 본문으로 보낸다.
 *
 * @example
 * api.get<Card[]>("/api/corpcard/cards");
 * api.get<Page>("/api/aegis/requests", { status: "WAIT", page: 1 });
 * api.post<MaskCardResponse>("/api/corpcard/cards/mask", { cardNo });
 * api.upload<UploadResult>("/api/aegis/transfers", { files: [a, b] });
 * api.download("/api/aegis/docsecurity/release", { file });
 */
export const api = {
	/** 조회. JSON 응답의 data 를 돌려준다. */
	get: <T>(path: string, params?: QueryParams) => requestJson<T>("GET", buildUrl(path, params)),

	/** 등록 등. 본문은 JSON 으로 보낸다. 응답에 값이 없으면 T 를 생략한다. */
	post: <T = void>(path: string, body?: unknown) => requestJson<T>("POST", path, body),

	/** 수정. 본문은 JSON 으로 보낸다. */
	put: <T = void>(path: string, body?: unknown) => requestJson<T>("PUT", path, body),

	/** 삭제 */
	delete: <T = void>(path: string) => requestJson<T>("DELETE", path),

	/** 파일을 올리고 JSON 결과를 받는다. */
	upload: <T = void>(path: string, fields: FormFields) => requestUpload<T>(path, fields),

	/**
	 * 파일을 받는다. fields 를 주면 올린 뒤(POST) 결과 파일을 받고, 주지 않으면 GET 으로 받는다.
	 * <p>받은 파일은 {@link saveFile} 로 저장한다.
	 */
	download: (path: string, fields?: FormFields) => requestDownload(path, fields),
};
