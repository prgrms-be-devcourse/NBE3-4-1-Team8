package com.example.backend.global.mail.service;

import java.util.Map;

import com.example.backend.global.mail.util.TemplateName;

/**
 * MailService
 * <p>메일 전송 서비스 인터페이스 입니다.</p>
 * @author Kim Dong O
 */
public interface MailService {
	/**
	 * @implSpec 비동기로 이메일을 전송 합니다.
	 * @param to 받는 사람 이메일
	 * @param htmlParameterMap 템플릿에 들어갈 파라미터
	 * @param templateName 템플릿 이름
	 */
	void sendEmail(String to, Map<String, String> htmlParameterMap, TemplateName templateName);
}
