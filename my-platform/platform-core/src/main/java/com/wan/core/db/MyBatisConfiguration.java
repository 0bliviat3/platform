package com.wan.core.db;

import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 기본 구성. EMP_CODE 컬럼을 empCode 로 자동 매핑한다. (01_backend_rules 2.2)
 */
@Configuration(proxyBeanMethods = false)
public class MyBatisConfiguration {

	@Bean
	ConfigurationCustomizer coreMyBatisCustomizer() {
		return configuration -> configuration.setMapUnderscoreToCamelCase(true);
	}
}
