package com.wan.core.health;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 서비스 상태 응답.
 *
 * @param status      서비스 상태 ("UP")
 * @param application 서비스 이름 (spring.application.name)
 * @param profiles    활성 프로파일 (없으면 기본 프로파일)
 * @param time        응답 시각
 */
public record HealthResponse(String status, String application, List<String> profiles, OffsetDateTime time) {
}
