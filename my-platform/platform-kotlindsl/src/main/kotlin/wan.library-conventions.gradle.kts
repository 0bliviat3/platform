// platform-common, platform-core 처럼 jar 로 쓰이는 라이브러리 모듈용
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
	id("wan.java-conventions")
	`java-library`
}

dependencies {
	// 버전은 Spring Boot BOM 이 관리한다.
	api(platform(SpringBootPlugin.BOM_COORDINATES))
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
