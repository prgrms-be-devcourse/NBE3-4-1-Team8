package com.example.backend.global.auth.service;

import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.domain.common.EmailCertification;
import com.example.backend.domain.common.VerifyType;
import com.example.backend.domain.member.dto.MemberDto;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.exception.MemberErrorCode;
import com.example.backend.domain.member.exception.MemberException;
import com.example.backend.domain.member.repository.MemberRepository;
import com.example.backend.global.auth.dto.AuthForm;
import com.example.backend.global.auth.exception.AuthErrorCode;
import com.example.backend.global.auth.exception.AuthException;
import com.example.backend.global.auth.jwt.JwtProvider;
import com.example.backend.global.redis.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private static final String REDIS_EMAIL_PREFIX = "certification_email:";
	private final JwtProvider jwtProvider;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenService refreshTokenService;
	private final MemberRepository memberRepository;
	private final RedisService redisService;
	private final ObjectMapper objectMapper;

    public String login(AuthForm authForm) {
        MemberDto findMember = memberRepository.findByUsername(authForm.getUsername())
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND)).toModel();

        if (!passwordEncoder.matches(authForm.getPassword(), findMember.password())) {
            throw new AuthException(AuthErrorCode.PASSWORD_NOT_MATCH);
        }

        if (MemberStatus.PENDING.equals(findMember.memberStatus())) {
            throw new AuthException(AuthErrorCode.NOT_CERTIFICATION);
        }

        String accessToken = jwtProvider.generateAccessToken(findMember.id(), findMember.username(), findMember.role());
        String refreshToken = jwtProvider.generateRefreshToken(findMember.id(), findMember.username());
        refreshTokenService.saveRefreshToken(findMember.username(), refreshToken);

		return accessToken + " " + refreshToken;
	}

	public void verify(String username, String certificationCode, VerifyType verifyType) {
		handleVerify(username, certificationCode, verifyType);

        MemberDto findMember = memberRepository.findByUsername(username)
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND)).toModel();

		MemberDto verifyMember = findMember.verify();

		memberRepository.save(Member.from(verifyMember));
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

	public void logout(String accessToken) {
		String username = jwtProvider.getUsernameFromToken(accessToken);
		refreshTokenService.deleteRefreshToken(username);

		// 시큐리티 컨텍스트 초기화
		SecurityContextHolder.clearContext();
	}
}
