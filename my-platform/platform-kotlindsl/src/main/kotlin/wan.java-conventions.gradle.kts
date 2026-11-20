// 모든 모듈 공통 Java 설정 (라이브러리/앱 convention 이 이를 포함한다)
plugins {
	java
}

group = "com.wan"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

// Lombok: 컴파일할 때만 코드를 만들어 주는 도구라서 배포 파일에는 들어가지 않는다.
// 버전은 libs.versions.toml 에서만 관리한다. (Spring Boot BOM 이 관리하는 버전과 맞춰 둔다)
val lombok = extensions.getByType<VersionCatalogsExtension>().named("libs").findLibrary("lombok").get()

dependencies {
	compileOnly(lombok)
	annotationProcessor(lombok)
	testCompileOnly(lombok)
	testAnnotationProcessor(lombok)
}

tasks.withType<JavaCompile>().configureEach {
	options.encoding = "UTF-8"
	// MyBatis @Param 생략, Spring 파라미터 이름 바인딩에 필요
	options.compilerArgs.add("-parameters")
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()
}
