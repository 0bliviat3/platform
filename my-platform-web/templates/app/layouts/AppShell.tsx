import type { ReactNode } from "react";
import { SideNav } from "./SideNav";
import { TOP_MENU } from "./menu";

/**
 * 포털 공통 틀. 상단 바(서비스 메뉴·사용자)와 왼쪽 메뉴, 본문 영역으로 이루어진다.
 * <p>상단 메뉴의 다른 서비스와 사용자 정보는 포털 모양을 보여주는 예시이며, 실제 SSO·메뉴 연동은 확정 전이다.
 */
export function AppShell({ children }: { children: ReactNode }) {
	return (
		<div className="grid min-h-screen grid-rows-[3.5rem_auto_1fr] md:grid-cols-[var(--spacing-sidebar)_1fr] md:grid-rows-[3.5rem_1fr]">
			<header className="sticky top-0 z-10 flex items-center gap-3 bg-nav px-3 text-nav-fg-strong md:col-span-2 md:gap-6 md:px-6">
				<span className="text-lg font-extrabold tracking-widest">PSNET</span>
				<nav aria-label="서비스 선택" className="hidden md:block">
					<ul className="flex gap-1">
						{TOP_MENU.map((item) => (
							<li
								key={item.label}
								className={`cursor-default rounded-md px-4 py-2 ${item.active ? "bg-nav-active font-semibold text-nav-fg-strong" : "text-nav-fg"}`}
							>
								{item.label}
							</li>
						))}
					</ul>
				</nav>
				<div className="ml-auto flex items-center gap-2 text-sm">
					<span
						className="inline-flex size-8 items-center justify-center rounded-full bg-primary font-bold"
						aria-hidden="true"
					>
						홍
					</span>
					<span>홍길동 (예시)</span>
				</div>
			</header>
			<aside className="border-t border-nav-active bg-nav">
				<SideNav />
			</aside>
			<main className="min-w-0 p-4 md:p-6 lg:p-8">{children}</main>
		</div>
	);
}
