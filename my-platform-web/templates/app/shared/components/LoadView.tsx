import type { ReactNode } from "react";
import type { LoadState } from "@/shared/hooks/useLoad";

interface LoadViewProps<T> {
	state: LoadState<T>;
	/** 데이터가 준비됐을 때 보여줄 화면 */
	children: (data: T) => ReactNode;
}

/**
 * 로딩 중 / 오류 / 완료 상태를 한 곳에서 보여준다.
 */
export function LoadView<T>({ state, children }: LoadViewProps<T>) {
	if (state.error) {
		return (
			<p className="rounded-md bg-danger-bg p-4 text-danger" role="alert">
				{state.error}
			</p>
		);
	}
	if (!state.data) {
		return (
			<p className="text-ink-muted" role="status">
				불러오는 중...
			</p>
		);
	}
	return <>{children(state.data)}</>;
}
