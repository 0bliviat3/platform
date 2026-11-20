pluginManagement {
	// 각 모듈의 gradle 을 연결해 주는 프로젝트 (convention 플러그인)
	includeBuild("platform-kotlindsl")
}

dependencyResolutionManagement {
	repositories {
		mavenCentral()
	}
}

rootProject.name = "my-platform"

// platform-* 폴더 중 build.gradle.kts 가 있는 것은 자동으로 모듈에 포함한다.
// 새 서비스는 tools/new-domain.sh 로 만들면 이 파일을 고칠 필요가 없다.
rootDir.listFiles()
	?.filter {
		it.isDirectory && it.name.startsWith("platform-") && it.name != "platform-kotlindsl"
			&& File(it, "build.gradle.kts").exists()
	}
	?.sortedBy { it.name }
	?.forEach { include(it.name) }
