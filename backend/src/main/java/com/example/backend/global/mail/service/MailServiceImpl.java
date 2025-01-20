package com.example.backend.global.mail.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.backend.domain.common.EmailCertification;
import com.example.backend.global.mail.util.MailSender;
import com.example.backend.global.mail.util.TemplateMaker;
import com.example.backend.global.mail.util.TemplateName;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
	@Value("${mail.verify-url}")
	private String verifyUrl;
	private final TemplateMaker templateMaker;
	private final MailSender mailSender;

	@Async("threadPoolTaskExecutor")
	@Override
	public void sendCertificationMail(String to, EmailCertification emailCertification, TemplateName templateName) {
		StringBuilder titleBuilder = new StringBuilder();
		Map<String, String> htmlParameterMap = new HashMap<>();
		switch (templateName) {
			case TemplateName.PASSWORD_RESET -> {
				titleBuilder.append("[TEAM8] 임시 비밀번호 입니다.");
			}
			case TemplateName.PASSWORD_RESET_VERIFY -> {
				titleBuilder.append("[TEAM8] 비밀번호 초기화 인증번호 입니다.");
			}
			case TemplateName.SIGNUP_VERIFY -> {
				titleBuilder.append("[TEAM8] 이메일 인증 메일 입니다.");

				String certificationUrl = generateCertificationUrl(to, emailCertification.getCertificationCode(),
					emailCertification.getVerifyType());

				htmlParameterMap.put("certificationUrl", certificationUrl);

			}
		}

		String title = titleBuilder.toString();

		MimeMessage mimeMessage = templateMaker.create(mailSender.createMimeMessage(), to, title, htmlParameterMap,
			templateName);

		mailSender.send(mimeMessage);
	}

	@Async("threadPoolTaskExecutor")
	@Override
	public void sendTemporaryPasswordMail(String to, String temporaryPassword, TemplateName templateName) {
		StringBuilder titleBuilder = new StringBuilder();
		Map<String, String> htmlParameterMap = new HashMap<>();
		switch (templateName) {
			case TemplateName.PASSWORD_RESET -> {
				titleBuilder.append("[TEAM8] 임시 비밀번호 입니다.");
				htmlParameterMap.put("temporaryPassword", temporaryPassword);
			}
		}

		String title = titleBuilder.toString();

		MimeMessage mimeMessage = templateMaker.create(mailSender.createMimeMessage(), to, title, htmlParameterMap,
			templateName);

		mailSender.send(mimeMessage);
	}

	@Async("threadPoolTaskExecutor")
	@Override
	public void sendDeliveryStartEmail(List<String> to, TemplateName templateName) {
		StringBuilder titleBuilder = new StringBuilder();

		switch (templateName) {
			case TemplateName.DELIVERY_START -> {
				titleBuilder.append("[TEAM8] 배송 시작 메일 입니다.");
			}
		}

		String title = titleBuilder.toString();

		MimeMessage mimeMessage = templateMaker.create(mailSender.createMimeMessage(), to, title, templateName);

		mailSender.send(mimeMessage);
	}

	private String generateCertificationUrl(String to, String certificationCode, String verifyType) {
		return verifyUrl + "username=" + to + "&certificationCode="
			+ certificationCode + "&verifyType=" + verifyType;
	}
}
