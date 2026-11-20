package com.wan.common.file;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 서버에 저장할 파일 이름 생성기. 사용자가 올린 이름은 저장 이름으로 쓰지 않는다.
 * <p>저장 이름은 난수(UUID)이고 확장자만 허용 목록에서 가져온다. 원본 이름은 DB 에 따로 기록해서 화면에만 보여 준다.
 * <p>사용처: 파일 업로드 저장 단계
 */
public final class StoredFileNames {

	private static final Pattern EXTENSION = Pattern.compile("[a-z0-9]{1,10}");

	private StoredFileNames() {
	}

	/**
	 * 저장용 파일 이름을 만든다.
	 *
	 * @param originalName      사용자가 올린 파일 이름 (경로가 섞여 있어도 마지막 이름만 본다)
	 * @param allowedExtensions 허용 확장자 목록 (점 없이 소문자, 예: "pdf", "xlsx")
	 * @return "난수.확장자" 형태의 이름
	 * @throws IllegalArgumentException 이름이 비었거나, 확장자가 없거나, 허용 목록에 없는 경우
	 */
	public static String randomName(String originalName, Set<String> allowedExtensions) {
		if (originalName == null || originalName.isBlank() || originalName.indexOf('\0') >= 0) {
			throw new IllegalArgumentException("파일 이름이 올바르지 않습니다.");
		}
		// 경로 구분자(/ 와 \) 뒤의 마지막 이름만 본다.
		int slash = Math.max(originalName.lastIndexOf('/'), originalName.lastIndexOf('\\'));
		String name = originalName.substring(slash + 1);

		int dot = name.lastIndexOf('.');
		if (dot <= 0 || dot == name.length() - 1) {
			throw new IllegalArgumentException("확장자가 없는 파일은 올릴 수 없습니다.");
		}
		String extension = name.substring(dot + 1).toLowerCase(Locale.ROOT);
		if (!EXTENSION.matcher(extension).matches() || !allowedExtensions.contains(extension)) {
			throw new IllegalArgumentException("허용되지 않는 파일 형식입니다.");
		}
		return UUID.randomUUID() + "." + extension;
	}
}
