package com.example.backend.global.mail.service;

import java.util.List;

import com.example.backend.domain.common.EmailCertification;
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
	 * @param emailCertification 이메일 인증 객체
	 * @param templateName 템플릿 이름
	 */
	void sendCertificationMail(String to, EmailCertification emailCertification, TemplateName templateName);
	/**
	 * @implSpec 비동기로 이메일을 전송 합니다.
	 * @param to 받는 사람 이메일
	 * @param temporaryPassword 임시 비밀번호
	 * @param templateName 템플릿 이름
	 */
	void sendTemporaryPasswordMail(String to, String temporaryPassword, TemplateName templateName);
	/**
	 * @implSpec 비동기로 다수의 회원에게 이메일을 전송 합니다.
	 * @param to 받는 사람 이메일들의 이메일
	 * @param templateName 템플릿 이름
	 */
	void sendDeliveryStartEmail(List<String> to, TemplateName templateName);
}
