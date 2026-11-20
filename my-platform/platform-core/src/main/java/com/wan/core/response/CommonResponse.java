package com.wan.core.response;

import com.wan.core.exception.ErrorCode;

/**
 * 공통 API 응답 형식.
 * <p>사용처: 모든 도메인의 REST 컨트롤러 응답, 공통 예외 처리기
 *
 * @param success 성공 여부
 * @param code    성공이면 "OK", 실패면 {@link ErrorCode#code()}
 * @param message 성공이면 null, 실패면 오류 메시지
 * @param data    응답 본문 (없으면 null)
 * @param <T>     본문 타입
 */
public record CommonResponse<T>(boolean success, String code, String message, T data) {

	/**
	 * @param data 응답 본문
	 * @return 성공 응답
	 */
	public static <T> CommonResponse<T> ok(T data) {
		return new CommonResponse<>(true, "OK", null, data);
	}

	/**
	 * @return 본문 없는 성공 응답
	 */
	public static CommonResponse<Void> ok() {
		return new CommonResponse<>(true, "OK", null, null);
	}

	/**
	 * @param errorCode 오류 코드
	 * @param message   오류 메시지
	 * @return 실패 응답
	 */
	public static CommonResponse<Void> fail(ErrorCode errorCode, String message) {
		return new CommonResponse<>(false, errorCode.code(), message, null);
	}
}
