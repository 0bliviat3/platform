package com.wan.core.exception;

import org.springframework.http.HttpStatus;

/**
 * 오류 코드 규약. 도메인은 이 인터페이스를 구현한 enum 으로 자기 오류 코드를 정의한다.
 * <p>사용처: {@link BusinessException}, 공통 예외 처리기
 */
public interface ErrorCode {

	/** @return 클라이언트에 내려가는 오류 코드 (예: BOARD_NOT_FOUND) */
	String code();

	/** @return 기본 오류 메시지 */
	String message();

	/** @return 응답 HTTP 상태 */
	HttpStatus status();
}
