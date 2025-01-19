package com.example.backend.global.auth.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.domain.common.EmailCertification;
import com.example.backend.domain.common.VerifyType;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.entity.MemberStatus;
import com.example.backend.domain.member.exception.MemberErrorCode;
import com.example.backend.domain.member.exception.MemberException;
import com.example.backend.domain.member.repository.MemberRepository;
import com.example.backend.global.auth.dto.AuthForm;
import com.example.backend.global.auth.dto.AuthResponse;
import com.example.backend.global.auth.exception.AuthErrorCode;
import com.example.backend.global.auth.exception.AuthException;
import com.example.backend.global.auth.jwt.JwtProvider;
import com.example.backend.global.auth.jwt.JwtUtils;
import com.example.backend.global.mail.service.MailService;
import com.example.backend.global.mail.util.TemplateName;
import com.example.backend.global.redis.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
	private static final String REDIS_EMAIL_PREFIX = "certification_email:";
	private final JwtProvider jwtProvider;
	private final JwtUtils jwtUtils;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenService refreshTokenService;
	private final MemberRepository memberRepository;
	private final RedisService redisService;
	private final MailService mailService;
	private final ObjectMapper objectMapper;

	public AuthResponse login(AuthForm authForm) {
		Member findMember = memberRepository.findByUsername(authForm.username())
			.orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

		if (!passwordEncoder.matches(authForm.password(), findMember.getPassword())) {
			throw new AuthException(AuthErrorCode.PASSWORD_NOT_MATCH);
		}

		if (!MemberStatus.ACTIVE.equals(findMember.getMemberStatus())) {
			throw new AuthException(AuthErrorCode.NOT_CERTIFICATION);
		}

		String accessToken = jwtProvider.generateAccessToken(findMember.getId(), findMember.getUsername(), findMember.getRole());
		String refreshToken = jwtProvider.generateRefreshToken(findMember.getId(), findMember.getUsername(),
			findMember.getRole());
		refreshTokenService.saveRefreshToken(findMember.getUsername(), refreshToken);

		return AuthResponse.of(findMember.getUsername(), accessToken, refreshToken);
	}

	public void logout(String accessToken) {
		String username = jwtUtils.getUsernameFromToken(accessToken);
		refreshTokenService.deleteRefreshToken(username);

		// 시큐리티 컨텍스트 초기화
		SecurityContextHolder.clearContext();
	}

	public void verify(String username, String certificationCode, VerifyType verifyType) {
		handleVerify(username, certificationCode, verifyType);
		Member findMember = memberRepository.findByUsername(username)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

		// 비밀번호 초기화 코드면 인증 후 임시 비밀번호 발송 로직 추가할 것
		switch (verifyType) {
			case SIGNUP -> {
				if (MemberStatus.ACTIVE.equals(findMember.getMemberStatus())) {
					throw new AuthException(AuthErrorCode.ALREADY_CERTIFIED);
				}
				findMember.verify();

				memberRepository.save(findMember);
			}
			case PASSWORD_RESET -> {
				String createTemporaryPassword = Member.createTemporaryPassword();
				findMember.changePassword(createTemporaryPassword);
				memberRepository.save(findMember);

				mailService.sendTemporaryPasswordMail(username, createTemporaryPassword, TemplateName.PASSWORD_RESET);
			}
		}
	}

	private void handleVerify(String username, String certificationCode, VerifyType verifyType) {
		//인증 코드가 존재하지 않을 때
		Map<Object, Object> getEmailCertification = redisService.getHashDataAll(REDIS_EMAIL_PREFIX + username);

		if (getEmailCertification.isEmpty()) {
			throw new AuthException(AuthErrorCode.CERTIFICATION_CODE_NOT_FOUND);
		}

		EmailCertification emailCertification = objectMapper.convertValue(getEmailCertification,
			EmailCertification.class);

		//인증 타입이 일치하지 않을 때
		if (!emailCertification.getVerifyType().equalsIgnoreCase(verifyType.toString())) {
			throw new AuthException(AuthErrorCode.VERIFY_TYPE_NOT_MATCH);
		}

		//인증 코드가 일치하지 않을 때
		if (!emailCertification.getCertificationCode().equals(certificationCode)) {
			throw new AuthException(AuthErrorCode.CERTIFICATION_CODE_NOT_MATCH);
		}

		redisService.delete(REDIS_EMAIL_PREFIX + username);
	}

	public void send(String username, VerifyType verifyType) {
		TemplateName templateName =
			VerifyType.SIGNUP.equals(verifyType) ? TemplateName.SIGNUP_VERIFY : TemplateName.PASSWORD_RESET_VERIFY;

		Member findMember = memberRepository.findByUsername(username)
			.orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

		String certificationCode = UUID.randomUUID().toString();

		Map<?, ?> findHashDataAll = redisService.getHashDataAll(REDIS_EMAIL_PREFIX + username);

		//인증 코드를 처음 발급하는지 확인
		if (findHashDataAll.isEmpty()) {
			EmailCertification emailCertification = EmailCertification.builder()
				.certificationCode(certificationCode)
				.sendCount("1")
				.verifyType(verifyType.toString())
				.build();

			mailService.sendCertificationMail(username, emailCertification, templateName);

			redisService.setHashDataAll(
				REDIS_EMAIL_PREFIX + username, objectMapper.convertValue(emailCertification, Map.class)
			);

			redisService.setTimeout(REDIS_EMAIL_PREFIX + username, 10);
		} else {
			EmailCertification findEmailCertification = objectMapper.convertValue(findHashDataAll,
				EmailCertification.class);

			// 인증 코드 타입이 일치하지 않을 때 EXCEPTION
			if (!verifyType.toString().equalsIgnoreCase(findEmailCertification.getVerifyType())) {
				throw new AuthException(AuthErrorCode.VERIFY_TYPE_NOT_MATCH);
			}

			//5회 이상 요청했을 때 EXCEPTION
			if (Integer.parseInt(findEmailCertification.getSendCount()) >= 5) {
				throw new AuthException(AuthErrorCode.TOOMANY_RESEND_ATTEMPTS);
			}

			findEmailCertification.addResendCount();
			findEmailCertification.setCertificationCode(certificationCode);

			mailService.sendCertificationMail(username, findEmailCertification, templateName);

			redisService.setHashDataAll(
				REDIS_EMAIL_PREFIX + username, objectMapper.convertValue(findEmailCertification, Map.class)
			);

			redisService.setTimeout(REDIS_EMAIL_PREFIX + username, 10);
		}
	}
}
