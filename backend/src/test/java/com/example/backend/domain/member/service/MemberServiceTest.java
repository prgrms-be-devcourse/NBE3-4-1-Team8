package com.example.backend.domain.member.service;

import static org.mockito.BDDMockito.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.backend.domain.common.Address;
import com.example.backend.domain.member.dto.MemberDto;
import com.example.backend.domain.member.dto.MemberSignupRequest;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.entity.Role;
import com.example.backend.domain.member.exception.MemberErrorCode;
import com.example.backend.domain.member.exception.MemberException;
import com.example.backend.domain.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private MemberService memberService;

	@DisplayName("회원가입 성공 테스트")
	@Test
	void signup_success() {
		//given
		MemberSignupRequest givenMemberSignupRequest = MemberSignupRequest.builder()
			.username("testEmail@naver.com")
			.nickname("testNickName")
			.password("!testPassword1234")
			.passwordCheck("!testPassword1234")
			.city("testCity")
			.detail("testDetail")
			.country("testCountry")
			.district("testDistrict")
			.verifyCode("testCode")
			.build();

		Address givenAddress = Address.builder()
			.city(givenMemberSignupRequest.city())
			.detail(givenMemberSignupRequest.detail())
			.country(givenMemberSignupRequest.country())
			.district(givenMemberSignupRequest.district())
			.build();

		MemberDto givenMember = MemberDto.builder()
			.username(givenMemberSignupRequest.username())
			.nickname(givenMemberSignupRequest.nickname())
			.password(givenMemberSignupRequest.password())
			.address(givenAddress)
			.role(Role.ROLE_USER)
			.build();

		Member givenMemberEntity = Member.from(givenMember);

		given(passwordEncoder.encode(givenMemberSignupRequest.password())).willReturn(
			givenMemberSignupRequest.password());
		given(memberRepository.existsByNickname(givenMember.nickname())).willReturn(false);
		given(memberRepository.existsByUsername(givenMember.username())).willReturn(false);
		given(memberRepository.save(any(Member.class))).willReturn(givenMemberEntity);

		//when
		memberService.signup(givenMemberSignupRequest.username(), givenMemberSignupRequest.nickname(),
			givenMemberSignupRequest.password(), givenMemberSignupRequest.passwordCheck(),
			givenMemberSignupRequest.city(), givenMemberSignupRequest.district(), givenMemberSignupRequest.country(),
			givenMemberSignupRequest.detail());

		//then
		verify(passwordEncoder, times(1)).encode(givenMemberSignupRequest.password());
		verify(memberRepository, times(1)).existsByNickname(givenMember.nickname());
		verify(memberRepository, times(1)).existsByUsername(givenMember.username());
		verify(memberRepository, times(1)).save(any(Member.class));
	}

	@DisplayName("회원가입 이메일 중복 실패 테스트")
	@Test
	void signup_exists_username_fail() {
		MemberSignupRequest givenMemberSignupRequest = MemberSignupRequest.builder()
			.username("testEmail@naver.com")
			.nickname("testNickName")
			.password("!testPassword1234")
			.passwordCheck("!testPassword1234")
			.city("testCity")
			.detail("testDetail")
			.country("testCountry")
			.district("testDistrict")
			.verifyCode("testCode")
			.build();

		Address givenAddress = Address.builder()
			.city(givenMemberSignupRequest.city())
			.detail(givenMemberSignupRequest.detail())
			.country(givenMemberSignupRequest.country())
			.district(givenMemberSignupRequest.district())
			.build();

		MemberDto givenMember = MemberDto.builder()
			.username(givenMemberSignupRequest.username())
			.nickname(givenMemberSignupRequest.nickname())
			.password(givenMemberSignupRequest.password())
			.address(givenAddress)
			.role(Role.ROLE_USER)
			.build();

		Member givenMemberEntity = Member.from(givenMember);

		given(memberRepository.existsByUsername(givenMember.username())).willReturn(true);
		given(memberRepository.existsByNickname(givenMember.nickname())).willReturn(false);

		//when & then
		Assertions.assertThatThrownBy(() -> memberService.signup(givenMemberSignupRequest.username(), givenMemberSignupRequest.nickname(),
			givenMemberSignupRequest.password(), givenMemberSignupRequest.passwordCheck(),
			givenMemberSignupRequest.city(), givenMemberSignupRequest.district(), givenMemberSignupRequest.country(),
			givenMemberSignupRequest.detail()))
			.isInstanceOf(MemberException.class)
			.hasMessage(MemberErrorCode.EXISTS_USERNAME.getMessage());
	}
}