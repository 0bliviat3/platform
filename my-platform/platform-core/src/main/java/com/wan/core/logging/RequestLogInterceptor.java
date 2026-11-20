package com.wan.core.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * API 요청 로그. 요청이 들어올 때와 끝날 때 한 줄씩 남겨서 어느 컨트롤러가 호출됐는지, 얼마나 걸렸는지 보이게 한다.
 * <pre>
 * → POST /api/aegis/docsecurity/release [DocSecurityController.release]
 * ← 200 POST /api/aegis/docsecurity/release (820 ms)
 * </pre>
 * <p>남기는 것은 메소드, 경로, 호출된 컨트롤러, 상태, 걸린 시간뿐이다. 쿼리 문자열, 헤더, 본문, 파일 이름은 남기지 않는다.
 * (주소 뒤의 값이나 본문에 비밀값·개인정보가 들어 있을 수 있다)
 * <p>사용처: 모든 도메인의 컨트롤러 호출 (core 자동 구성이 등록)
 */
@Slf4j
@NullMarked
public class RequestLogInterceptor implements HandlerInterceptor {

	private static final String STARTED_AT = RequestLogInterceptor.class.getName() + ".startedAt";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if (handler instanceof HandlerMethod method) {
			request.setAttribute(STARTED_AT, System.nanoTime());
			log.info("→ {} {} [{}]", request.getMethod(), request.getRequestURI(), name(method));
		}
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
		@Nullable Exception ex) {
		if (!(handler instanceof HandlerMethod) || !(request.getAttribute(STARTED_AT) instanceof Long startedAt)) {
			return;
		}
		long elapsedMs = (System.nanoTime() - startedAt) / 1_000_000;
		int status = response.getStatus();
		// 예외는 공통 예외 처리기가 상태로 바꿔 기록하므로, 여기서는 상태로만 구분한다.
		if (status >= 400) {
			log.warn("← {} {} {} ({} ms)", status, request.getMethod(), request.getRequestURI(), elapsedMs);
		} else {
			log.info("← {} {} {} ({} ms)", status, request.getMethod(), request.getRequestURI(), elapsedMs);
		}
	}

	/** 호출된 컨트롤러 이름. 예) DocSecurityController.release */
	private static String name(HandlerMethod method) {
		return method.getBeanType().getSimpleName() + "." + method.getMethod().getName();
	}
}
