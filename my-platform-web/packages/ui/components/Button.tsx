import type { ButtonHTMLAttributes } from "react";

/**
 * 버튼의 종류.
 * - primary: 화면의 주된 동작 (화면당 하나를 권장)
 * - secondary: 보조 동작
 */
export type ButtonVariant = "primary" | "secondary";

export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
	/** 버튼 종류. 기본값은 primary */
	variant?: ButtonVariant;
}

const BASE =
	"inline-flex min-h-10 cursor-pointer items-center justify-center rounded-md border border-transparent px-4 " +
	"font-sans text-base font-semibold focus-visible:outline-2 focus-visible:outline-offset-2 " +
	"focus-visible:outline-focus disabled:cursor-not-allowed disabled:opacity-50";

const VARIANTS: Record<ButtonVariant, string> = {
	primary: "bg-primary text-primary-fg enabled:hover:bg-primary-hover",
	secondary: "border-line bg-surface text-ink enabled:hover:bg-page",
};

/**
 * 공통 버튼.
 * <p>도메인을 알지 못하며, 색상과 크기는 디자인 토큰(tokens.css)을 따른다.
 * 폼 안에서 쓰면 기본 type 은 submit 이므로, 제출이 아닌 동작에는 type="button" 을 지정한다.
 *
 * @example
 * <Button onClick={handleSave}>저장</Button>
 * <Button variant="secondary" type="button">취소</Button>
 */
export function Button({ variant = "primary", className, ...rest }: ButtonProps) {
	const classNames = [BASE, VARIANTS[variant], className].filter(Boolean).join(" ");
	return <button className={classNames} {...rest} />;
}
