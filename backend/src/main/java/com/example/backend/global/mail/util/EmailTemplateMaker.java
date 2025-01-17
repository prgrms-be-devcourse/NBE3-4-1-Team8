package com.example.backend.global.mail.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * TemplateMaker 구현체 입니다.
 * <p>이메일 템플릿을 만들어 반환합니다.</p>
 * @author : Kim Dong O
 */
@Slf4j
public class EmailTemplateMaker implements TemplateMaker {

	private final SpringTemplateEngine templateEngine;
	private Map<String, String> templateNameMap = new ConcurrentHashMap<>();

	public EmailTemplateMaker(SpringTemplateEngine templateEngine, Map<String, String> templateNameMap) {
		this.templateEngine = templateEngine;
		this.templateNameMap = templateNameMap;
	}

	@Override
	public MimeMessage create(MimeMessage newMimeMessage, String email, String title,
		Map<String, String> htmlParameterMap, TemplateName templateName) {
		try {
			MimeMessageHelper helper = new MimeMessageHelper(newMimeMessage, true, "UTF-8");

			Context context = new Context();

			//파라미터 값 설정
			htmlParameterMap.forEach(context::setVariable);

			String processedHtmlContent = templateEngine.process(templateNameMap.get(templateName.toString()), context);
			log.info("processedHtmlContent = {}", processedHtmlContent);

			helper.setTo(email);
			helper.setSubject(title);
			helper.setText(processedHtmlContent, true);

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

		return newMimeMessage;
	}
}
