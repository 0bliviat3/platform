// 서비스 모듈(실행 가능한 Spring Boot 앱)용. platform-core 를 반드시 사용한다.
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	id("wan.java-conventions")
	id("org.springframework.boot")
}

dependencies {
	implementation(platform(SpringBootPlugin.BOM_COORDINATES))
	implementation(project(":platform-core"))
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Jar>("jar") {
	enabled = false
}

// Dockerfile 이 고정된 이름으로 참조할 수 있게 한다. (build/libs/<모듈명>.jar)
tasks.named<BootJar>("bootJar") {
	archiveFileName = "${project.name}.jar"
}
