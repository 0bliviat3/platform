"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { SIDE_MENU } from "./menu";

const LINK_BASE =
	"block whitespace-nowrap rounded-md px-3 py-2 hover:bg-nav-active hover:text-nav-fg-strong " +
	"focus-visible:outline-2 focus-visible:-outline-offset-2 focus-visible:outline-nav-fg-strong";

/**
 * 왼쪽 메뉴. 현재 경로와 같은 항목을 강조한다.
 * 좁은 화면(md 미만)에서는 가로 메뉴로 바뀐다.
 */
export function SideNav() {
	const pathname = usePathname();
	return (
		<nav
			className="flex gap-4 overflow-x-auto px-3 py-2 md:flex-col md:gap-6 md:overflow-visible md:py-4"
			aria-label="서비스 메뉴"
		>
			{SIDE_MENU.map((group) => (
				<div key={group.title}>
					<p className="mb-2 ml-3 hidden text-sm text-nav-fg opacity-70 md:block">{group.title}</p>
					<ul className="flex gap-0.5 md:flex-col">
						{group.items.map((item) => {
							const active = pathname === item.href;
							return (
								<li key={item.href}>
									<Link
										href={item.href}
										className={`${LINK_BASE} ${active ? "bg-nav-active font-semibold text-nav-fg-strong" : "text-nav-fg"}`}
										aria-current={active ? "page" : undefined}
									>
										{item.label}
									</Link>
								</li>
							);
						})}
					</ul>
				</div>
			))}
		</nav>
	);
}
