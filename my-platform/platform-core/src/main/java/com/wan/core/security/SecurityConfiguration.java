package com.wan.core.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 보안 기본 구성. 도메인이 SecurityFilterChain 을 직접 정의하면 이 구성은 물러난다. (SSO 연동은 도메인/후속 작업)
 * <p>{@code wan.security.open=true} (local 프로파일)이면 모든 요청을 허용한다.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfiguration {

	/** Boot 가 임시 계정(user + 로그에 찍히는 비밀번호)을 만들지 못하게 빈 사용자 저장소를 둔다. */
	@Bean
	@ConditionalOnMissingBean(UserDetailsService.class)
	InMemoryUserDetailsManager emptyUserDetailsService() {
		return new InMemoryUserDetailsManager();
	}

	@Bean
	@ConditionalOnMissingBean(SecurityFilterChain.class)
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http,
		@Value("${wan.security.open:false}") boolean open) throws Exception {
		if (open) {
			http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
		} else {
			http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/actuator/health/**", "/actuator/info", "/api/health").permitAll()
				.anyRequest().authenticated());
		}
		return http.build();
	}
}
