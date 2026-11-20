import { useId } from "react";
import type { InputHTMLAttributes } from "react";

export interface TextFieldProps extends Omit<InputHTMLAttributes<HTMLInputElement>, "id"> {
	/** 입력 항목 이름. 화면에 표시되고 입력창과 연결된다 (접근성) */
	label: string;
	/** 입력 방법 안내 문구 */
	hint?: string;
	/** 오류 메시지. 값이 있으면 입력창이 오류 상태로 표시된다 */
	error?: string;
}

const INPUT =
	"min-h-10 rounded-md border bg-surface px-3 font-sans text-base text-ink " +
	"focus-visible:outline-2 focus-visible:outline-offset-1 focus-visible:outline-focus";

/**
 * 공통 텍스트 입력창.
 * <p>라벨, 안내 문구, 오류 메시지를 한 묶음으로 보여준다. 라벨과 입력창은 자동으로 연결되고,
 * 오류가 있으면 보조기기에 알려진다 (aria-invalid, aria-describedby).
 *
 * @example
 * <TextField label="카드번호" hint="숫자 16자리" value={cardNo} onChange={handleChange} error={error} />
 */
export function TextField({ label, hint, error, className, ...rest }: TextFieldProps) {
	const id = useId();
	const messageId = `${id}-message`;
	const hasMessage = Boolean(error || hint);
	const inputClassNames = [INPUT, error ? "border-danger" : "border-line", className].filter(Boolean).join(" ");

	return (
		<div className="flex flex-col gap-1">
			<label className="text-sm font-semibold text-ink" htmlFor={id}>
				{label}
			</label>
			<input
				id={id}
				className={inputClassNames}
				aria-invalid={error ? true : undefined}
				aria-describedby={hasMessage ? messageId : undefined}
				{...rest}
			/>
			{error ? (
				<span id={messageId} className="text-sm text-danger" role="alert">
					{error}
				</span>
			) : hint ? (
				<span id={messageId} className="text-sm text-ink-muted">
					{hint}
				</span>
			) : null}
		</div>
	);
}
