package com.wan.common.file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * 파일 해시 계산기. 감사 로그에는 파일 내용 대신 이 해시를 남긴다.
 * <p>사용처: 업로드·해제·전송 로그 기록
 */
public final class FileHashes {

	private static final int BUFFER_SIZE = 8192;

	private FileHashes() {
	}

	/**
	 * 파일의 SHA-256 해시를 계산한다. 파일 전체를 메모리에 올리지 않고 조금씩 읽는다.
	 *
	 * @param file 대상 파일
	 * @return 소문자 16진수 64자
	 * @throws IOException 파일을 읽을 수 없는 경우
	 */
	public static String sha256Hex(Path file) throws IOException {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// 모든 JDK 가 SHA-256 을 제공하므로 일어날 수 없다.
			throw new IllegalStateException(e);
		}
		try (InputStream in = Files.newInputStream(file)) {
			byte[] buffer = new byte[BUFFER_SIZE];
			int read;
			while ((read = in.read(buffer)) != -1) {
				digest.update(buffer, 0, read);
			}
		}
		return HexFormat.of().formatHex(digest.digest());
	}
}
