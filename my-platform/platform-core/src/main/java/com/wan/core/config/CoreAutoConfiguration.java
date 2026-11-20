package com.wan.core.config;

import com.wan.core.db.MyBatisConfiguration;
import com.wan.core.exception.GlobalExceptionHandler;
import com.wan.core.health.HealthController;
import com.wan.core.logging.RequestLogConfiguration;
import com.wan.core.security.SecurityConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * core 진입점. 도메인 앱은 core 를 의존성으로 추가하는 것만으로 공통 구성을 받는다.
 * (META-INF/spring/...AutoConfiguration.imports 에 등록)
 */
@AutoConfiguration(beforeName = "org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration")
@Import({GlobalExceptionHandler.class, SecurityConfiguration.class, MyBatisConfiguration.class,
	HealthController.class, RequestLogConfiguration.class})
public class CoreAutoConfiguration {
}
