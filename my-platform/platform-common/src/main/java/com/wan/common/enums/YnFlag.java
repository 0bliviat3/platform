package com.wan.common.enums;

/**
 * Y/N 구분값. DB의 DEL_YN, USE_YN 같은 플래그 컬럼과 대응한다.
 * <p>사용처: 모든 도메인의 논리 삭제/사용 여부 컬럼
 */
public enum YnFlag {
	/** 예 (DB 코드 'Y') */
	Y,
	/** 아니오 (DB 코드 'N') */
	N;

	/**
	 * 불리언을 Y/N 으로 변환한다.
	 *
	 * @param value 변환할 값
	 * @return true 면 {@link #Y}, false 면 {@link #N}
	 */
	public static YnFlag of(boolean value) {
		return value ? Y : N;
	}

	/**
	 * @return {@link #Y} 이면 true
	 */
	public boolean isYes() {
		return this == Y;
	}
}
