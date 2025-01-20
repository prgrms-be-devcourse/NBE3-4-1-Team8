package com.example.backend.global.mail.util;

import java.util.List;
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
	 * @param username
	 * @param title
	 * @param htmlParameterMap
	 * @param templateName
	 * @return {@link MimeMessage}
	 */
	MimeMessage create(MimeMessage newMimeMessage, String username, String title,
		Map<String, String> htmlParameterMap, TemplateName templateName);

	/**
	 * @implSpec 파라미터 값이 없는 메일을 전송할 때 사용하며 템플릿, 타이틀을 설정하여 반환합니다.
	 * @param newMimeMessage
	 * @param usernameList
	 * @param title
	 * @param templateName
	 * @return {@link MimeMessage}
	 */
	MimeMessage create(MimeMessage newMimeMessage, List<String> usernameList, String title, TemplateName templateName);
}
