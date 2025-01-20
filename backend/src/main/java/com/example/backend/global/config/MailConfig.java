package com.example.backend.global.config;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.example.backend.global.mail.util.EmailTemplateMaker;
import com.example.backend.global.mail.util.TemplateMaker;
import com.example.backend.global.mail.util.TemplateName;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@Getter
@RequiredArgsConstructor
public class MailConfig {

	@Value("${mail.host}")
	private String mailHost;
	@Value("${mail.port}")
	private int mailPort;
	@Value("${mail.username}")
	private String mailUsername;
	@Value("${mail.password}")
	private String mailPassword;
	@Value("${mail.properties.mail.smtp.auth}")
    private boolean smtpAuth;

    @Value("${mail.properties.mail.smtp.starttls.enable}")
    private boolean smtpStartTlsEnable;

    @Value("${mail.templates.path}")
    private String templatesPath;

    @Value("${mail.templates.password-reset}")
    private String passwordReset;

	@Value("${mail.templates.email-verify}")
    private String emailVerify;

	@Value("${mail.templates.signup-verify}")
    private String signupVerify;

	@Value("${mail.templates.delivery-start}")
	private String deliveryStart;


	@Bean
	public JavaMailSender mailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(mailHost);
		mailSender.setPort(mailPort);
		mailSender.setUsername(mailUsername);
		mailSender.setPassword(mailPassword);
		Properties props = System.getProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.starttls.enable", smtpStartTlsEnable);
		mailSender.setJavaMailProperties(props);
		return mailSender;
	}

	@Bean
	public TemplateMaker emailTemplateMaker() {
		Map<String, String> templateNameMap = new ConcurrentHashMap<>();

		//각 템플릿 이름 Map에 저장
		templateNameMap.put(TemplateName.PASSWORD_RESET.toString(), passwordReset);
		templateNameMap.put(TemplateName.PASSWORD_RESET_VERIFY.toString(), emailVerify);
		templateNameMap.put(TemplateName.SIGNUP_VERIFY.toString(), signupVerify);
		templateNameMap.put(TemplateName.DELIVERY_START.toString(), deliveryStart);

		EmailTemplateMaker emailTemplateMaker = new EmailTemplateMaker(
			thymeleafTemplateEngine(),
			templateNameMap
		);

		return emailTemplateMaker;
	}

	@Bean
	public SpringTemplateEngine thymeleafTemplateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(thymeleafTemplateResolver());
		return templateEngine;
	}

	@Bean
	public ITemplateResolver thymeleafTemplateResolver() {
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setPrefix(templatesPath);
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode("HTML");
		templateResolver.setCharacterEncoding("UTF-8");
		return templateResolver;
	}
}
