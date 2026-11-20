import type { NextConfig } from "next";
import { PHASE_DEVELOPMENT_SERVER } from "next/constants";

/**
 * __TITLE__ 프론트 설정.
 * <p>basePath: 서버에서 Nginx 가 /__NAME__ 경로를 이 앱으로 보내므로 앱 주소를 /__NAME__ 아래로 맞춘다.
 * <p>/api 프록시: 브라우저는 항상 같은 도메인의 /api 로 백엔드를 호출한다. 서버에서는 Nginx 가 /api 를
 * 백엔드로 넘기고, 로컬 개발(next dev)에서는 아래 rewrites 가 그 역할을 대신한다. 운영 빌드에는 포함하지 않는다.
 */
export default function nextConfig(phase: string): NextConfig {
	const config: NextConfig = {
		basePath: "/__NAME__",
		cacheComponents: true,
		partialPrefetching: true,
	};

	if (phase === PHASE_DEVELOPMENT_SERVER) {
		const apiBaseUrl = process.env.API_BASE_URL ?? "http://localhost:__API_PORT__";
		config.rewrites = async () => [
			// basePath: false — /api 는 /__NAME__ 아래가 아니라 도메인 루트의 경로이므로 basePath 를 붙이지 않는다.
			{ source: "/api/:path*", destination: `${apiBaseUrl}/api/:path*`, basePath: false },
		];
	}

	return config;
}
