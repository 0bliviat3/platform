"use client";

import { useEffect, useState } from "react";
import { getErrorMessage } from "@wan/api";

/** 데이터 로드 상태. 로딩 중이면 data, error 가 모두 null 이다. */
export interface LoadState<T> {
	data: T | null;
	error: string | null;
}

/**
 * 화면이 열릴 때 API 를 한 번 호출해 결과를 돌려준다.
 * <p>loader 는 매 렌더마다 새로 만들면 계속 다시 호출되므로, 모듈 수준 함수(api 함수)를 넘긴다.
 * 화면이 닫힌 뒤 도착한 응답은 버린다.
 * <p>사용처: 기능별 조회 훅(features/<기능>/hooks)
 *
 * @param loader 데이터를 가져오는 함수
 * @returns 로드 상태 (data / error)
 */
export function useLoad<T>(loader: () => Promise<T>): LoadState<T> {
	const [state, setState] = useState<LoadState<T>>({ data: null, error: null });

	useEffect(() => {
		let cancelled = false;
		loader()
			.then((data) => {
				if (!cancelled) {
					setState({ data, error: null });
				}
			})
			.catch((e: unknown) => {
				if (!cancelled) {
					setState({
						data: null,
						error: getErrorMessage(e),
					});
				}
			});
		return () => {
			cancelled = true;
		};
	}, [loader]);

	return state;
}
