package com.wan.core.health;

import com.wan.core.response.CommonResponse;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 모든 서비스가 공통으로 제공하는 상태 확인 API.
 * <p>DB 등 외부 의존을 확인하지 않고 "앱이 요청을 받을 수 있는지"만 알려준다.
 * 배포 헬스체크는 {@code /actuator/health} 를 쓰고, 이 API 는 사람이 서비스 이름과 프로파일을 확인하는 용도다.
 */
@RestController
@RequestMapping("/api")
public class HealthController {

	private final String applicationName;
	private final Environment environment;

	public HealthController(@Value("${spring.application.name:unknown}") String applicationName,
		Environment environment) {
		this.applicationName = applicationName;
		this.environment = environment;
	}

	/**
	 * 서비스 상태를 반환한다.
	 *
	 * @return 상태, 서비스 이름, 활성 프로파일, 현재 시각
	 */
	@GetMapping("/health")
	public CommonResponse<HealthResponse> health() {
		List<String> profiles = Arrays.asList(environment.getActiveProfiles());
		if (profiles.isEmpty()) {
			profiles = Arrays.asList(environment.getDefaultProfiles());
		}
		return CommonResponse.ok(new HealthResponse("UP", applicationName, profiles, OffsetDateTime.now()));
	}
}
