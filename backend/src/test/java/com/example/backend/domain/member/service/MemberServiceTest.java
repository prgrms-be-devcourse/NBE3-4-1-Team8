package com.example.backend.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.backend.domain.common.Address;
import com.example.backend.domain.member.conveter.MemberConverter;
import com.example.backend.domain.member.dto.MemberDto;
import com.example.backend.domain.member.dto.MemberInfoResponse;
import com.example.backend.domain.member.dto.MemberModifyForm;
import com.example.backend.domain.member.dto.MemberSignupForm;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.entity.MemberStatus;
import com.example.backend.domain.member.entity.Role;
import com.example.backend.domain.member.exception.MemberErrorCode;
import com.example.backend.domain.member.exception.MemberException;
import com.example.backend.domain.member.repository.MemberRepository;
import com.example.backend.global.mail.service.MailService;
import com.example.backend.global.redis.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
	@Mock
	private MemberRepository memberRepository;

	@Mock
	private RedisService redisService;

	@Mock
	private MailService mailService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private ObjectMapper objectMapper;

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
			givenMemberSignupForm.password(), givenMemberSignupForm.city(), givenMemberSignupForm.district(),
			givenMemberSignupForm.country(), givenMemberSignupForm.detail());

		//then
		verify(passwordEncoder, times(1)).encode(givenMemberSignupForm.password());
		verify(memberRepository, times(1)).existsByNickname(givenMember.nickname());
		verify(memberRepository, times(1)).existsByUsername(givenMember.username());
		verify(memberRepository, times(1)).save(any(Member.class));
	}

	@DisplayName("비밀번호 변경 성공 테스트")
	@Test
	void password_change_success() {
	    //given
		Address givenAddress = Address.builder()
			.city("testCity")
			.detail("testDetail")
			.country("testCountry")
			.district("testDistrict")
			.build();

		Member givenMember = Member.builder()
			.username("testUsername")
			.nickname("testNickname")
			.password("!testPassword1234")
			.address(givenAddress)
			.memberStatus(MemberStatus.ACTIVE)
			.role(Role.ROLE_USER)
			.build();

		String changePassword = "!changePassword1234";

		Member changePasswordMember = givenMember.changePassword(changePassword);

		given(passwordEncoder.matches(changePassword, givenMember.getPassword())).willReturn(true);
		given(passwordEncoder.encode(changePassword)).willReturn(changePassword);
		given(memberRepository.save(changePasswordMember)).willReturn(changePasswordMember);

	    //when
		memberService.passwordChange(givenMember.getPassword(), changePassword, givenMember);

	    //then
		verify(passwordEncoder, times(1)).matches(changePassword, givenMember.getPassword());
		verify(passwordEncoder, times(1)).encode(changePassword);
		verify(memberRepository, times(1)).save(changePasswordMember);
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
		assertThatThrownBy(() -> memberService.signup(
				givenMemberSignupForm.username(), givenMemberSignupForm.nickname(),
				givenMemberSignupForm.password(), givenMemberSignupForm.city(), givenMemberSignupForm.district(),
				givenMemberSignupForm.country(), givenMemberSignupForm.detail()))
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
		assertThatThrownBy(() -> memberService.signup(
				givenMemberSignupForm.username(), givenMemberSignupForm.nickname(),
				givenMemberSignupForm.password(), givenMemberSignupForm.city(), givenMemberSignupForm.district(),
				givenMemberSignupForm.country(), givenMemberSignupForm.detail()))
			.isInstanceOf(MemberException.class)
			.hasMessage(MemberErrorCode.EXISTS_NICKNAME.getMessage());
	}

	@Test
	@DisplayName("회원 정보 수정 성공 테스트")
	void modify_success() {
		// given
		Address address = Address.builder()
			.city("testCity")
			.district("testDistrict")
			.country("testCountry")
			.detail("testDetail")
			.build();

		MemberDto memberDto = MemberDto.builder()
			.nickname("testNickName")
			.address(address)
			.build();

		MemberModifyForm memberModifyForm = MemberModifyForm.builder()
			.nickname("updatedNickName")
			.city("updatedCity")
			.district("updatedDistrict")
			.country("updatedCountry")
			.detail("updatedDetail")
			.build();

		Member member = Member.from(MemberConverter.of(memberDto, memberModifyForm));
		when(memberRepository.existsByNickname(memberModifyForm.nickname())).thenReturn(false);
		when(memberRepository.save(any(Member.class))).thenReturn(member);

		// when
		MemberInfoResponse result = memberService.modify(memberDto, memberModifyForm);

		// then
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(MemberInfoResponse.class);
		assertThat(result.nickname()).isEqualTo(memberModifyForm.nickname());
		assertThat(result.address().getCity()).isEqualTo(memberModifyForm.city());
		assertThat(result.address().getDistrict()).isEqualTo(memberModifyForm.district());
		assertThat(result.address().getCountry()).isEqualTo(memberModifyForm.country());
		assertThat(result.address().getDetail()).isEqualTo(memberModifyForm.detail());
	}

	@Test
	@DisplayName("회원 정보 수정 실패 - 닉네임 중복")
	void modify_fail_nickname_already_exists() {
		// given
		Address address = Address.builder()
			.city("testCity")
			.district("testDistrict")
			.country("testCountry")
			.detail("testDetail")
			.build();

		MemberDto memberDto = MemberDto.builder()
			.nickname("testNickName")
			.address(address)
			.build();

		MemberModifyForm memberModifyForm = MemberModifyForm.builder()
			.nickname("updatedNickName")
			.city("updatedCity")
			.district("updatedDistrict")
			.country("updatedCountry")
			.detail("updatedDetail")
			.build();

		when(memberRepository.existsByNickname(memberModifyForm.nickname())).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> memberService.modify(memberDto, memberModifyForm))
			.isInstanceOf(MemberException.class)
			.hasMessage(MemberErrorCode.EXISTS_NICKNAME.getMessage());
	}
}