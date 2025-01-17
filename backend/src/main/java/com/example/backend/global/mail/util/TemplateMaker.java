package com.example.backend.global.mail.util;

import java.util.Map;

import jakarta.mail.internet.MimeMessage;

/**
 * TemplateMaker 인터페이스 입니다.
 * <p>이메일 템플릿을 만들어 반환합니다.</p>
 * @author Kim Dong O
 */
public interface TemplateMaker {

	/**
	 * @implSpec MimeMessage에 사용할 템플릿, 변수 등을 설정하여 반환합니다.
	 * @param newMimeMessage
	 * @param email
	 * @param title
	 * @param htmlParameterMap
	 * @param templateName
	 * @return {@link MimeMessage}
	 */
	public MimeMessage create(MimeMessage newMimeMessage, String email, String title,
		Map<String, String> htmlParameterMap, TemplateName templateName);
}
