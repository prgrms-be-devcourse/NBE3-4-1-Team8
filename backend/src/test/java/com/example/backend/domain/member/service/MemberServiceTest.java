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
import com.example.backend.domain.member.dto.MemberSignupForm;
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
		MemberSignupForm givenMemberSignupForm = MemberSignupForm.builder()
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
			.city(givenMemberSignupForm.city())
			.detail(givenMemberSignupForm.detail())
			.country(givenMemberSignupForm.country())
			.district(givenMemberSignupForm.district())
			.build();

		MemberDto givenMember = MemberDto.builder()
			.username(givenMemberSignupForm.username())
			.nickname(givenMemberSignupForm.nickname())
			.password(givenMemberSignupForm.password())
			.address(givenAddress)
			.role(Role.ROLE_USER)
			.build();

		Member givenMemberEntity = Member.from(givenMember);

		given(passwordEncoder.encode(givenMemberSignupForm.password())).willReturn(
			givenMemberSignupForm.password());
		given(memberRepository.existsByNickname(givenMember.nickname())).willReturn(false);
		given(memberRepository.existsByUsername(givenMember.username())).willReturn(false);
		given(memberRepository.save(any(Member.class))).willReturn(givenMemberEntity);

		//when
		memberService.signup(givenMemberSignupForm.username(), givenMemberSignupForm.nickname(),
			givenMemberSignupForm.password(), givenMemberSignupForm.passwordCheck(),
			givenMemberSignupForm.city(), givenMemberSignupForm.district(), givenMemberSignupForm.country(),
			givenMemberSignupForm.detail());

		//then
		verify(passwordEncoder, times(1)).encode(givenMemberSignupForm.password());
		verify(memberRepository, times(1)).existsByNickname(givenMember.nickname());
		verify(memberRepository, times(1)).existsByUsername(givenMember.username());
		verify(memberRepository, times(1)).save(any(Member.class));
	}

	@DisplayName("회원가입 이메일 중복 실패 테스트")
	@Test
	void signup_exists_username_fail() {
		//given
		MemberSignupForm givenMemberSignupForm = MemberSignupForm.builder()
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
			.city(givenMemberSignupForm.city())
			.detail(givenMemberSignupForm.detail())
			.country(givenMemberSignupForm.country())
			.district(givenMemberSignupForm.district())
			.build();

		MemberDto givenMember = MemberDto.builder()
			.username(givenMemberSignupForm.username())
			.nickname(givenMemberSignupForm.nickname())
			.password(givenMemberSignupForm.password())
			.address(givenAddress)
			.role(Role.ROLE_USER)
			.build();

		given(memberRepository.existsByUsername(givenMember.username())).willReturn(true);
		given(memberRepository.existsByNickname(givenMember.nickname())).willReturn(false);

		//when & then
		Assertions.assertThatThrownBy(() -> memberService.signup(
				givenMemberSignupForm.username(), givenMemberSignupForm.nickname(),
			givenMemberSignupForm.password(), givenMemberSignupForm.passwordCheck(),
			givenMemberSignupForm.city(), givenMemberSignupForm.district(), givenMemberSignupForm.country(),
			givenMemberSignupForm.detail()))
			.isInstanceOf(MemberException.class)
			.hasMessage(MemberErrorCode.EXISTS_USERNAME.getMessage());
	}

	@DisplayName("회원가입 닉네임 중복 실패 테스트")
	@Test
	void signup_exists_nickname_fail() {
		//given
		MemberSignupForm givenMemberSignupForm = MemberSignupForm.builder()
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
			.city(givenMemberSignupForm.city())
			.detail(givenMemberSignupForm.detail())
			.country(givenMemberSignupForm.country())
			.district(givenMemberSignupForm.district())
			.build();

		MemberDto givenMember = MemberDto.builder()
			.username(givenMemberSignupForm.username())
			.nickname(givenMemberSignupForm.nickname())
			.password(givenMemberSignupForm.password())
			.address(givenAddress)
			.role(Role.ROLE_USER)
			.build();

		given(memberRepository.existsByUsername(givenMember.username())).willReturn(false);
		given(memberRepository.existsByNickname(givenMember.nickname())).willReturn(true);

		//when & then
		Assertions.assertThatThrownBy(() -> memberService.signup(
				givenMemberSignupForm.username(), givenMemberSignupForm.nickname(),
			givenMemberSignupForm.password(), givenMemberSignupForm.passwordCheck(),
			givenMemberSignupForm.city(), givenMemberSignupForm.district(), givenMemberSignupForm.country(),
			givenMemberSignupForm.detail()))
			.isInstanceOf(MemberException.class)
			.hasMessage(MemberErrorCode.EXISTS_NICKNAME.getMessage());
	}
}