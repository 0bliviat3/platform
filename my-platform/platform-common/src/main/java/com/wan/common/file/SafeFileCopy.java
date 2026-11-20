package com.wan.common.file;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;

/**
 * 덮어쓰지 않는 파일 복사.
 * <p>결과 위치에 이미 파일이 있으면 실패한다. 덮어쓰기 옵션을 쓰지 않아서, 같은 위치에 동시에 쓰려는 요청이 있어도
 * 한쪽만 성공한다.
 * <p>사용처: 해제 결과 저장, 전송용 파일 보관
 */
public final class SafeFileCopy {

	private SafeFileCopy() {
	}

	/**
	 * 원본을 대상 위치로 복사한다. 원본은 바꾸지 않는다.
	 *
	 * @param source 원본 (일반 파일이어야 함)
	 * @param target 복사할 위치 (아직 없어야 하고, 부모 폴더는 있어야 함)
	 * @throws NoSuchFileException        원본이 없거나 일반 파일이 아닌 경우
	 * @throws NotDirectoryException      대상의 부모 폴더가 없는 경우
	 * @throws FileAlreadyExistsException 대상 위치에 이미 파일이 있는 경우
	 * @throws IOException                그 밖의 입출력 오류
	 */
	public static void copyNoOverwrite(Path source, Path target) throws IOException {
		if (!Files.isRegularFile(source)) {
			throw new NoSuchFileException(String.valueOf(source.getFileName()));
		}
		Path parent = target.toAbsolutePath().getParent();
		if (parent == null || !Files.isDirectory(parent)) {
			throw new NotDirectoryException(String.valueOf(target.getFileName()));
		}
		Files.copy(source, target);
	}
}
