package com.wan.common.file;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * 경로 조작(Path Traversal) 방지용 경로 생성기.
 * <p>사용자가 보낸 이름이나 경로를 서버가 정한 저장 폴더 아래에서만 해석한다.
 * <p>경로 구분자는 '/' 만 허용하며, 역슬래시와 드라이브 문자(C:)가 들어간 입력은 어느 운영체제에서든 거부한다.
 * <p>한계: 심볼릭 링크는 따라가지 않고 판단하지 않는다. 저장 폴더 안에 링크를 만들 수 없게 서버 권한으로 통제한다.
 * <p>사용처: 파일 업로드·다운로드·해제 결과 저장 등 사용자 입력이 파일 경로로 이어지는 모든 곳
 */
public final class SafePaths {

	/** "C:" 처럼 드라이브 문자로 시작하는 입력 */
	private static final Pattern DRIVE_PREFIX = Pattern.compile("^[A-Za-z]:");

	private SafePaths() {
	}

	/**
	 * 기준 폴더 아래의 경로를 만든다. 기준 폴더 밖으로 나가거나 기준 폴더 자체를 가리키면 거부한다.
	 *
	 * @param baseDir  서버가 정한 기준(저장) 폴더
	 * @param relative 기준 폴더 아래의 상대 경로 (사용자 입력일 수 있음)
	 * @return 정규화된 절대 경로
	 * @throws IllegalArgumentException 비어 있거나, 절대 경로이거나, 기준 폴더 밖으로 나가는 경우
	 */
	public static Path resolveUnder(Path baseDir, String relative) {
		if (baseDir == null) {
			throw new IllegalArgumentException("기준 폴더가 없습니다.");
		}
		if (relative == null || relative.isBlank() || relative.indexOf('\0') >= 0) {
			throw new IllegalArgumentException("경로가 올바르지 않습니다.");
		}
		// 운영체제에 따라 해석이 달라지지 않도록 Windows 식 입력(역슬래시, 드라이브 문자)은 직접 막는다.
		// 경로 구분자는 '/' 만 허용한다.
		if (relative.indexOf('\\') >= 0 || DRIVE_PREFIX.matcher(relative).find()) {
			throw new IllegalArgumentException("경로가 올바르지 않습니다.");
		}
		return resolveInsideBase(baseDir, relative);
	}

	// 내부폴더 경로 확인
	private static Path resolveInsideBase(Path baseDir, String relative) {
		Path base = baseDir.toAbsolutePath().normalize();
		Path rel;
		try {
			rel = Path.of(relative);
		} catch (InvalidPathException e) {
			throw new IllegalArgumentException("경로가 올바르지 않습니다.");
		}
		// 절대 경로, 드라이브 문자(C:), 루트(\ 또는 /) 로 시작하는 입력은 모두 거부한다.
		if (rel.isAbsolute() || rel.getRoot() != null) {
			throw new IllegalArgumentException("절대 경로는 사용할 수 없습니다.");
		}
		Path resolved = base.resolve(rel).normalize();
		if (!resolved.startsWith(base) || resolved.equals(base)) {
			throw new IllegalArgumentException("허용된 폴더 밖의 경로입니다.");
		}
		return resolved;
	}
}
