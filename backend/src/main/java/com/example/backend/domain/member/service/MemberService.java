package com.example.backend.domain.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.domain.common.Address;
import com.example.backend.domain.member.dto.MemberDto;
import com.example.backend.domain.member.dto.MemberSignupResponse;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.entity.Role;
import com.example.backend.domain.member.exception.MemberErrorCode;
import com.example.backend.domain.member.exception.MemberException;
import com.example.backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public void signup(String username, String nickname, String password, String verifyCode,
		String city, String district, String country, String detail) {
		existsMember(username, nickname);

		//TODO 추후에 인증 메일 발송 기능 구현 후 수정 예정

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
			.role(Role.ROLE_USER)
			.address(saveAddress)
			.build();

		memberRepository.save(Member.from(saveMemberDto)).toModel();
	}

	private void existsMember(String username, String nickname) {
		boolean usernameExists = memberRepository.existsByUsername(username);
		boolean nicknameExists = memberRepository.existsByNickname(nickname);

		if (usernameExists) {
			throw new MemberException(MemberErrorCode.EXISTS_USERNAME);
		}

		if (nicknameExists) {
			throw new MemberException(MemberErrorCode.EXISTS_NICKNAME);
		}
	}
}
