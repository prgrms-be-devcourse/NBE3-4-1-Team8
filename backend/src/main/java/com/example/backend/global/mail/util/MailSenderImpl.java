package com.example.backend.global.mail.util;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

/**
 * MailSender
 * <p>MailSender 구현체 입니다.</p>
 * @author Kim Dong O
 */
@Component
@RequiredArgsConstructor
public class MailSenderImpl implements MailSender {
	private final JavaMailSender javaMailSender;

	@Override
	public void send(MimeMessage mimeMessage) {
		javaMailSender.send(mimeMessage);
	}

	@Override
	public MimeMessage createMimeMessage() {
		return javaMailSender.createMimeMessage();
	}
}