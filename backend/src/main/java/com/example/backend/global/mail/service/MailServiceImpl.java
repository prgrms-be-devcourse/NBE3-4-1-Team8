package com.example.backend.global.mail.service;

import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.backend.global.mail.util.MailSender;
import com.example.backend.global.mail.util.TemplateMaker;
import com.example.backend.global.mail.util.TemplateName;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService{
	private final TemplateMaker templateMaker;
	private final MailSender mailSender;

	@Async("threadPoolTaskExecutor")
	@Override
	public void sendEmail(String to, Map<String, String> htmlParameterMap, TemplateName templateName) {
		StringBuilder titleBuilder = new StringBuilder();

		switch (templateName) {
			case TemplateName.PASSWORD_RESET -> {
				titleBuilder.append("[TEAM8] 임시 비밀번호 입니다.");
			}
			case TemplateName.PASSWORD_RESET_VERIFY -> {
				titleBuilder.append("[TEAM8] 비밀번호 초기화 인증번호 입니다.");
			}
			case TemplateName.SIGNUP_VERIFY -> {
				titleBuilder.append("[TEAM8] 이메일 인증 메일 입니다.");
			}
		}

		String title = titleBuilder.toString();

		MimeMessage mimeMessage = templateMaker.create(mailSender.createMimeMessage(), to, title, htmlParameterMap,
			templateName);

		mailSender.send(mimeMessage);
	}
}
