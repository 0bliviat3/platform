/** 왼쪽 메뉴 항목 */
export interface SideMenuItem {
	label: string;
	/** basePath(/__NAME__) 를 뺀 경로 */
	href: string;
}

/** 왼쪽 메뉴 묶음 */
export interface SideMenuGroup {
	title: string;
	items: SideMenuItem[];
}

/** 상단 메뉴 항목. 현재 서비스(__TITLE__) 외에는 포털 모양을 보여주는 예시이며 이동하지 않는다. */
export interface TopMenuItem {
	label: string;
	active?: boolean;
}

export const TOP_MENU: TopMenuItem[] = [
	{ label: "__TITLE__", active: true },
	{ label: "전자결재" },
	{ label: "게시판" },
];

/** 화면을 추가하면 여기에 메뉴를 한 줄 더한다. */
export const SIDE_MENU: SideMenuGroup[] = [
	{
		title: "__TITLE__",
		items: [{ label: "홈", href: "/" }],
	},
];
