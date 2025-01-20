package com.example.backend.domain.member.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.domain.common.Address;
import com.example.backend.domain.common.EmailCertification;
import com.example.backend.domain.common.VerifyType;
import com.example.backend.domain.member.conveter.MemberConverter;
import com.example.backend.domain.member.dto.MemberDto;
import com.example.backend.domain.member.dto.MemberInfoResponse;
import com.example.backend.domain.member.dto.MemberModifyForm;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.entity.MemberStatus;
import com.example.backend.domain.member.entity.Role;
import com.example.backend.domain.member.exception.MemberErrorCode;
import com.example.backend.domain.member.exception.MemberException;
import com.example.backend.domain.member.repository.MemberRepository;
import com.example.backend.global.mail.service.MailService;
import com.example.backend.global.mail.util.TemplateName;
import com.example.backend.global.redis.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
	private static final String REDIS_CERTIFICATION_PREFIX = "certification_email:";
	private final MemberRepository memberRepository;
	private final RedisService redisService;
	private final MailService mailService;
	private final PasswordEncoder passwordEncoder;
	private final ObjectMapper objectMapper;

	public void signup(String username, String nickname, String password, String city, String district, String country,
		String detail) {
		existsMember(username, nickname);

		//TODO 추후에 인증 메일 발송 기능 구현 후 수정 예정

		String certificationCode = UUID.randomUUID().toString();

		EmailCertification emailCertification = EmailCertification.builder()
			.sendCount("1")
			.certificationCode(certificationCode)
			.verifyType(VerifyType.SIGNUP.toString())
			.build();

		Map convertValue = objectMapper.convertValue(emailCertification, Map.class);

		redisService.setHashDataAll(REDIS_CERTIFICATION_PREFIX + username, convertValue);
		redisService.setTimeout(REDIS_CERTIFICATION_PREFIX + username, 10);

		mailService.sendCertificationMail(username, emailCertification, TemplateName.SIGNUP_VERIFY);

		Address saveAddress = Address.builder()
			.city(city)
			.district(district)
			.country(country)
			.detail(detail)
			.build();

		MemberDto saveMemberDto = MemberDto.builder()
			.username(username)
			.nickname(nickname)
			.password(passwordEncoder.encode(password))
			.memberStatus(MemberStatus.PENDING)
			.role(Role.ROLE_USER)
			.address(saveAddress)
			.build();

		memberRepository.save(Member.from(saveMemberDto)).toModel();
	}

	public void passwordChange(String originalPassword, String changePassword, Member loginMember) {

		if (!passwordEncoder.matches(originalPassword, loginMember.getPassword())) {
			throw new MemberException(MemberErrorCode.PASSWORD_NOT_MATCH);
		}

		loginMember.changePassword(passwordEncoder.encode(changePassword));

		memberRepository.save(loginMember);
	}

	public MemberInfoResponse modify(MemberDto memberDto, MemberModifyForm memberModifyForm) {
		existsNickname(memberModifyForm.nickname());

		return MemberConverter.from(
			memberRepository.save(MemberConverter.of(memberDto, memberModifyForm)));
	}

	private void existsMember(String username, String nickname) {
		boolean usernameExists = memberRepository.existsByUsername(username);
		existsNickname(nickname);

		if (usernameExists) {
			throw new MemberException(MemberErrorCode.EXISTS_USERNAME);
		}
	}


	private void existsNickname (String nickname) {
		boolean nicknameExists = memberRepository.existsByNickname(nickname);

		if (nicknameExists) {
			throw new MemberException(MemberErrorCode.EXISTS_NICKNAME);
		}
	}
}
