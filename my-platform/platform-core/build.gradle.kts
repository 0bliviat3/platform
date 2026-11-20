plugins {
	id("wan.library-conventions")
}

// platform-core: Spring 기반 인프라 (db, 공통 응답·예외, 보안 기본)
// 모든 서비스가 반드시 사용하며, DB 드라이버는 서비스가 고른다. (MariaDB / PostgreSQL)
dependencies {
	api(project(":platform-common"))
	api("org.springframework.boot:spring-boot-starter-webmvc")
	api("org.springframework.boot:spring-boot-starter-validation")
	api("org.springframework.boot:spring-boot-starter-actuator")
	api("org.springframework.boot:spring-boot-starter-security")
	api(libs.mybatis.spring.boot.starter)
}
