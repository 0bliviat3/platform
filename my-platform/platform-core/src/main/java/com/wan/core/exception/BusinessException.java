package com.wan.core.exception;

import lombok.Getter;

/**
 * 업무 규칙 위반 예외. 공통 예외 처리기가 {@link ErrorCode} 의 HTTP 상태와 코드로 응답한다.
 * <p>사용처: Service 계층에서 업무 오류를 알릴 때
 */
@Getter
public class BusinessException extends RuntimeException {

	private final ErrorCode errorCode;

	/**
	 * @param errorCode 오류 코드 (기본 메시지를 사용한다)
	 */
	public BusinessException(ErrorCode errorCode) {
		super(errorCode.message());
		this.errorCode = errorCode;
	}

	/**
	 * @param errorCode 오류 코드
	 * @param message   기본 메시지 대신 내려줄 상세 메시지
	 */
	public BusinessException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

}
