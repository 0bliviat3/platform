interface PageHeaderProps {
	title: string;
	/** 제목 아래 보조 설명 */
	description?: string;
}

/**
 * 본문 맨 위의 화면 제목. 화면마다 같은 모양으로 쓴다.
 */
export function PageHeader({ title, description }: PageHeaderProps) {
	return (
		<header className="mb-6">
			<h1 className="mb-1 text-xl font-bold">{title}</h1>
			{description && <p className="text-ink-muted">{description}</p>}
		</header>
	);
}
