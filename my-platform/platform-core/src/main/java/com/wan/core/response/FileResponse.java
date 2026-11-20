package com.wan.core.response;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;

/**
 * 파일 다운로드 응답. 파일은 JSON 으로 감쌀 수 없어서 {@link CommonResponse} 대신 이 응답을 돌려준다.
 * <p>컨트롤러에서는 한 줄로 쓴다: {@code return FileResponse.download(content, "계획서_해제.docx");}
 * <p>브라우저가 화면에 열지 않고 지정한 이름으로 저장하게 하는 헤더(Content-Disposition)를 붙인다.
 * 파일 종류(Content-Type)와 크기(Content-Length)는 스프링이 자동으로 채운다.
 * <p>사용처: 파일을 내려주는 모든 컨트롤러
 */
public final class FileResponse {

	private FileResponse() {
	}

	/**
	 * 파일 내용을 다운로드 응답으로 만든다.
	 *
	 * @param content  파일 내용
	 * @param fileName 사용자에게 보여 줄 저장 이름 (한글 가능. 서버 저장 이름이 아니다)
	 * @return 다운로드 응답
	 */
	public static ResponseEntity<Resource> download(byte[] content, String fileName) {
		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION,
				ContentDisposition.attachment().filename(fileName, StandardCharsets.UTF_8).build().toString())
			.body(new ByteArrayResource(content));
	}
}
