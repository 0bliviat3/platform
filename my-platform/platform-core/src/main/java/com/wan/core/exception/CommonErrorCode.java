package com.wan.core.exception;

import org.springframework.http.HttpStatus;

/**
 * 모든 도메인이 공유하는 공통 오류 코드.
 */
public enum CommonErrorCode implements ErrorCode {
	/** 요청 값이 올바르지 않음 */
	INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
	/** 인증되지 않음 */
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
	/** 권한 없음 */
	FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
	/** 대상을 찾을 수 없음 */
	NOT_FOUND(HttpStatus.NOT_FOUND, "대상을 찾을 수 없습니다."),
	/** 서버 내부 오류 */
	INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

	private final HttpStatus status;
	private final String message;

	CommonErrorCode(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

	@Override
	public String code() {
		return name();
	}

	@Override
	public String message() {
		return message;
	}

	@Override
	public HttpStatus status() {
		return status;
	}
}
