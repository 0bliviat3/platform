dependencyResolutionManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
	}
	versionCatalogs {
		// 루트와 같은 버전 카탈로그를 공유한다.
		create("libs") {
			from(files("../gradle/libs.versions.toml"))
		}
	}
}

rootProject.name = "platform-kotlindsl"
