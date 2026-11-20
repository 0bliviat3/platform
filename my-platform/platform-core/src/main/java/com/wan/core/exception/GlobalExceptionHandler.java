package com.wan.core.exception;

import com.wan.core.response.CommonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 공통 예외 처리기. 모든 도메인이 같은 오류 응답 형식({@link CommonResponse})을 쓰게 한다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/** 업무 예외: 오류 코드에 정의된 HTTP 상태로 응답한다. */
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<CommonResponse<Void>> handleBusiness(BusinessException e) {
		ErrorCode code = e.getErrorCode();
		return ResponseEntity.status(code.status()).body(CommonResponse.fail(code, e.getMessage()));
	}

	/** 입력 검증 실패: 첫 번째 필드 오류를 메시지로 내려준다. */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<CommonResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getFieldErrors().stream()
			.findFirst()
			.map(f -> f.getField() + ": " + f.getDefaultMessage())
			.orElse(CommonErrorCode.INVALID_REQUEST.message());
		CommonErrorCode code = CommonErrorCode.INVALID_REQUEST;
		return ResponseEntity.status(code.status()).body(CommonResponse.fail(code, message));
	}

	/** 요청 본문을 읽을 수 없음(JSON 형식 오류 등): 본문 내용은 메시지에 싣지 않는다. */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<CommonResponse<Void>> handleUnreadable(HttpMessageNotReadableException e) {
		CommonErrorCode code = CommonErrorCode.INVALID_REQUEST;
		return ResponseEntity.status(code.status()).body(CommonResponse.fail(code, "요청 본문을 읽을 수 없습니다."));
	}

	/** 예상하지 못한 오류: 내부 정보는 노출하지 않고 로그에만 남긴다. */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<CommonResponse<Void>> handleUnexpected(Exception e) {
		// Spring MVC 가 상태를 정해둔 예외(404, 405, 415 등)는 그 상태를 유지한다.
		if (e instanceof ErrorResponse er && er.getStatusCode().is4xxClientError()) {
			CommonErrorCode clientCode = er.getStatusCode().value() == 404
				? CommonErrorCode.NOT_FOUND : CommonErrorCode.INVALID_REQUEST;
			return ResponseEntity.status(er.getStatusCode()).body(CommonResponse.fail(clientCode, clientCode.message()));
		}
		log.error("처리되지 않은 예외", e);
		CommonErrorCode code = CommonErrorCode.INTERNAL_ERROR;
		return ResponseEntity.status(code.status()).body(CommonResponse.fail(code, code.message()));
	}
}
