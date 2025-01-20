package com.example.backend.global.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.backend.domain.common.Address;
import com.example.backend.domain.common.EmailCertification;
import com.example.backend.domain.common.VerifyType;
import com.example.backend.domain.member.dto.MemberDto;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.entity.MemberStatus;
import com.example.backend.domain.member.entity.Role;
import com.example.backend.domain.member.exception.MemberErrorCode;
import com.example.backend.domain.member.exception.MemberException;
import com.example.backend.domain.member.repository.MemberRepository;
import com.example.backend.global.auth.dto.AuthForm;
import com.example.backend.global.auth.dto.AuthResponse;
import com.example.backend.global.auth.dto.EmailCertificationForm;
import com.example.backend.global.auth.exception.AuthErrorCode;
import com.example.backend.global.auth.exception.AuthException;
import com.example.backend.global.auth.jwt.JwtProvider;
import com.example.backend.global.auth.jwt.JwtUtils;
import com.example.backend.global.mail.service.MailService;
import com.example.backend.global.mail.util.TemplateName;
import com.example.backend.global.redis.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private JwtProvider jwtProvider;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtUtils jwtUtils;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private RefreshTokenService refreshTokenService;

	@Mock
	private RedisService redisService;

	@Mock
	private MailService mailService;

	@Mock
	private ObjectMapper objectMapper;

	private ObjectMapper testObjectMapper = Jackson2ObjectMapperBuilder
		.json().build()
		.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	@InjectMocks
	private AuthService authService;

	@Test
	@DisplayName("로그인 성공")
	void loginSuccess() {
		//given
		MemberDto memberDto = MemberDto.builder()
			.id(1L)
			.username("user@gmail.com")
			.password("password")
			.role(Role.ROLE_USER)
			.memberStatus(MemberStatus.ACTIVE)
			.build();
		Member member = Member.from(memberDto);

		when(memberRepository.findByUsername(any(String.class))).thenReturn(Optional.of(member));
		when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);
		when(jwtProvider.generateAccessToken(any(Long.class), any(String.class), any(Role.class))).thenReturn(
			"access_token");
		when(jwtProvider.generateRefreshToken(any(Long.class), any(String.class), any(Role.class))).thenReturn(
			"refresh_token");
		doNothing().when(refreshTokenService).saveRefreshToken(any(String.class), any(String.class));

		AuthForm authForm = AuthForm.builder()
			.username("user@gmail.com")
			.password("password")
			.build();

		//when
		AuthResponse result = authService.login(authForm);

		//then
		assertThat(result.username()).isEqualTo("user@gmail.com");
		assertThat(result.accessToken()).isEqualTo("access_token");
		assertThat(result.refreshToken()).isEqualTo("refresh_token");
		verify(memberRepository).findByUsername("user@gmail.com");
		verify(passwordEncoder).matches("password", "password");
		verify(jwtProvider).generateAccessToken(1L, "user@gmail.com", Role.ROLE_USER);
		verify(jwtProvider).generateRefreshToken(1L, "user@gmail.com", Role.ROLE_USER);
		verify(refreshTokenService).saveRefreshToken("user@gmail.com", "refresh_token");
	}

	@Test
	@DisplayName("로그인 실패 - 이메일 미인증일 때")
	void loginFail_memberStatus_pending() {
		// given
		MemberDto memberDto = MemberDto.builder()
			.id(1L)
			.username("user@gmail.com")
			.password("password")
			.role(Role.ROLE_USER)
			.memberStatus(MemberStatus.PENDING)
			.build();
		Member member = Member.from(memberDto);

		when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);
		when(memberRepository.findByUsername("user@gmail.com"))
			.thenReturn(Optional.of(member));

		AuthForm authForm = AuthForm.builder()
			.username("user@gmail.com")
			.password("password")
			.build();

		// when & then
		assertThatThrownBy(() -> authService.login(authForm))
			.isInstanceOf(AuthException.class)
			.hasMessage(AuthErrorCode.NOT_CERTIFICATION.getMessage());

		verify(memberRepository).findByUsername("user@gmail.com");
	}

	@Test
	@DisplayName("로그인 실패 - 해당 유저가 없음")
	void loginFail_UserNotFound() {
		// given
		when(memberRepository.findByUsername("user@gmail.com"))
			.thenReturn(Optional.empty());

		AuthForm authForm = AuthForm.builder()
			.username("user@gmail.com")
			.password("password")
			.build();

		// when & then
		assertThatThrownBy(() -> authService.login(authForm))
			.isInstanceOf(AuthException.class)
			.hasMessage(AuthErrorCode.MEMBER_NOT_FOUND.getMessage());

		verify(memberRepository).findByUsername("user@gmail.com");
	}

	@Test
	@DisplayName("로그인 실패 - 비밀번호 불일치")
	void loginFail_PasswordNotMatch() {
		// given
		MemberDto memberDto = MemberDto.builder()
			.id(1L)
			.username("user@gmail.com")
			.password("password")
			.role(Role.ROLE_USER)
			.build();
		Member member = Member.from(memberDto);

		when(memberRepository.findByUsername("user@gmail.com")).thenReturn(Optional.of(member));
		when(passwordEncoder.matches("pw", "password")).thenReturn(false);

		AuthForm authForm = AuthForm.builder()
			.username("user@gmail.com")
			.password("pw")
			.build();

		// when & then
		assertThatThrownBy(() -> authService.login(authForm))
			.isInstanceOf(AuthException.class)
			.hasMessage(AuthErrorCode.PASSWORD_NOT_MATCH.getMessage());

		verify(memberRepository).findByUsername("user@gmail.com");
		verify(passwordEncoder).matches("pw", "password");
	}

	@DisplayName("이메일 인증 성공 테스트")
	@Test
	void verify_success() {
		//given
		String givenRedisPrefix = "certification_email:";

		Address givenAddress = Address.builder()
			.city("testCity")
			.detail("testDetail")
			.country("testCountry")
			.district("testDistrict")
			.build();

		Member givenMember = Member.builder()
			.username("testEmail@naver.com")
			.nickname("testNickName")
			.password("!testPassword1234")
			.address(givenAddress)
			.memberStatus(MemberStatus.PENDING)
			.role(Role.ROLE_USER)
			.build();

		Member verifyMember = Member.builder()
			.username("testEmail@naver.com")
			.nickname("testNickName")
			.password("!testPassword1234")
			.address(givenAddress)
			.memberStatus(MemberStatus.ACTIVE)
			.role(Role.ROLE_USER)
			.build();

		EmailCertificationForm givenEmailCertificationForm = EmailCertificationForm.builder()
			.certificationCode("testCode")
			.verifyType(VerifyType.SIGNUP)
			.username("testEmail@naver.com")
			.build();

		EmailCertification givenEmailCertification = EmailCertification.builder()
			.certificationCode(givenEmailCertificationForm.certificationCode())
			.verifyType(givenEmailCertificationForm.verifyType().toString())
			.sendCount("1")
			.build();

		Map<Object, Object> givenConvertMap = testObjectMapper.convertValue(givenEmailCertification, Map.class);

		given(redisService.getHashDataAll(givenRedisPrefix + givenEmailCertificationForm.username()))
			.willReturn(givenConvertMap);

		given(objectMapper.convertValue(givenConvertMap, EmailCertification.class)).willReturn(givenEmailCertification);

		given(memberRepository.findByUsername(givenEmailCertificationForm.username()))
			.willReturn(Optional.of(givenMember));

		given(memberRepository.save(any(Member.class))).willReturn(verifyMember);

		doNothing().when(redisService).delete(givenRedisPrefix + givenEmailCertificationForm.username());

		//when
		authService.verify(givenEmailCertificationForm.username(),
			givenEmailCertificationForm.certificationCode(),
			givenEmailCertificationForm.verifyType());

		//then
		verify(redisService, times(1)).getHashDataAll(givenRedisPrefix + givenEmailCertificationForm.username());
		verify(memberRepository, times(1)).findByUsername(givenEmailCertificationForm.username());
		verify(memberRepository, times(1)).save(any(Member.class));
		verify(redisService, times(1)).delete(givenRedisPrefix + givenEmailCertificationForm.username());
	}

	@DisplayName("이메일 인증시 인증 정보 없을 때 실패 테스트")
	@Test
	void verify_emailCertification_not_found_fail() {
		//given
		String givenRedisPrefix = "certification_email:";

		Address givenAddress = Address.builder()
			.city("testCity")
			.detail("testDetail")
			.country("testCountry")
			.district("testDistrict")
			.build();

		Member givenMember = Member.builder()
			.username("testEmail@naver.com")
			.nickname("testNickName")
			.password("!testPassword1234")
			.address(givenAddress)
			.memberStatus(MemberStatus.PENDING)
			.role(Role.ROLE_USER)
			.build();

		EmailCertificationForm givenEmailCertificationForm = EmailCertificationForm.builder()
			.certificationCode("testCode")
			.verifyType(VerifyType.SIGNUP)
			.username("testEmail@naver.com")
			.build();

		given(redisService.getHashDataAll(givenRedisPrefix + givenEmailCertificationForm.username()))
			.willReturn(Map.of());

		given(memberRepository.findByUsername(givenMember.getUsername())).willReturn(Optional.of(givenMember));

		//when & then
		assertThatThrownBy(() -> authService.verify(givenEmailCertificationForm.username(),
			givenEmailCertificationForm.certificationCode(), givenEmailCertificationForm.verifyType()))
			.isInstanceOf(AuthException.class)
			.hasMessage(AuthErrorCode.CERTIFICATION_CODE_NOT_FOUND.getMessage());
	}

	@DisplayName("이메일 인증시 인증 타입이 일치하지 않을 때 실패 테스트")
	@Test
	void verify_verify_type_not_match_fail() {
		//given
		String givenRedisPrefix = "certification_email:";

		Address givenAddress = Address.builder()
			.city("testCity")
			.detail("testDetail")
			.country("testCountry")
			.district("testDistrict")
			.build();

		Member givenMember = Member.builder()
			.username("testEmail@naver.com")
			.nickname("testNickName")
			.password("!testPassword1234")
			.address(givenAddress)
			.memberStatus(MemberStatus.PENDING)
			.role(Role.ROLE_USER)
			.build();

		EmailCertificationForm givenEmailCertificationForm = EmailCertificationForm.builder()
			.certificationCode("testCode")
			.verifyType(VerifyType.SIGNUP)
			.username("testEmail@naver.com")
			.build();

		EmailCertification givenEmailCertification = EmailCertification.builder()
			.verifyType(VerifyType.PASSWORD_RESET.toString())
			.certificationCode(givenEmailCertificationForm.certificationCode())
			.sendCount("1")
			.build();

		Map<Object, Object> givenConvertMap = testObjectMapper.convertValue(givenEmailCertification, Map.class);

		given(memberRepository.findByUsername(givenMember.getUsername())).willReturn(Optional.of(givenMember));

		given(redisService.getHashDataAll(givenRedisPrefix + givenEmailCertificationForm.username()))
			.willReturn(givenConvertMap);

		given(objectMapper.convertValue(givenConvertMap, EmailCertification.class)).willReturn(givenEmailCertification);

		//when & then
		assertThatThrownBy(() -> authService.verify(givenEmailCertificationForm.username(),
			givenEmailCertificationForm.certificationCode(), givenEmailCertificationForm.verifyType()))
			.isInstanceOf(AuthException.class)
			.hasMessage(AuthErrorCode.VERIFY_TYPE_NOT_MATCH.getMessage());
	}

	@DisplayName("이메일 인증시 인증 코드가 일치하지 않을 때 실패 테스트")
	@Test
	void verify_certification_not_match_fail() {
		//given
		String givenRedisPrefix = "certification_email:";

		Address givenAddress = Address.builder()
			.city("testCity")
			.detail("testDetail")
			.country("testCountry")
			.district("testDistrict")
			.build();

		Member givenMember = Member.builder()
			.username("testEmail@naver.com")
			.nickname("testNickName")
			.password("!testPassword1234")
			.address(givenAddress)
			.memberStatus(MemberStatus.PENDING)
			.role(Role.ROLE_USER)
			.build();

		EmailCertificationForm givenEmailCertificationForm = EmailCertificationForm.builder()
			.certificationCode("testCode")
			.verifyType(VerifyType.SIGNUP)
			.username("testEmail@naver.com")
			.build();

		EmailCertification givenEmailCertification = EmailCertification.builder()
			.verifyType(givenEmailCertificationForm.verifyType().toString())
			.certificationCode("notMatchCode")
			.sendCount("1")
			.build();

		Map<Object, Object> givenConvertMap = testObjectMapper.convertValue(givenEmailCertification, Map.class);

		given(memberRepository.findByUsername(givenMember.getUsername())).willReturn(Optional.of(givenMember));

		given(redisService.getHashDataAll(givenRedisPrefix + givenEmailCertificationForm.username()))
			.willReturn(givenConvertMap);

		given(objectMapper.convertValue(givenConvertMap, EmailCertification.class)).willReturn(givenEmailCertification);

		//when & then
		assertThatThrownBy(() -> authService.verify(givenEmailCertificationForm.username(),
			givenEmailCertificationForm.certificationCode(), givenEmailCertificationForm.verifyType()))
			.isInstanceOf(AuthException.class)
			.hasMessage(AuthErrorCode.CERTIFICATION_CODE_NOT_MATCH.getMessage());
	}

	@DisplayName("이메일 인증시 회원 조회 실패 테스트")
	@Test
	void verify_member_not_found_fail() {
		//given
		String givenRedisPrefix = "certification_email:";

		Address givenAddress = Address.builder()
			.city("testCity")
			.detail("testDetail")
			.country("testCountry")
			.district("testDistrict")
			.build();

		Member givenMember = Member.builder()
			.username("testEmail@naver.com")
			.nickname("testNickName")
			.password("!testPassword1234")
			.address(givenAddress)
			.memberStatus(MemberStatus.PENDING)
			.role(Role.ROLE_USER)
			.build();

		EmailCertificationForm givenEmailCertificationForm = EmailCertificationForm.builder()
			.certificationCode("testCode")
			.verifyType(VerifyType.SIGNUP)
			.username("testEmail@naver.com")
			.build();

		EmailCertification givenEmailCertification = EmailCertification.builder()
			.certificationCode(givenEmailCertificationForm.certificationCode())
			.verifyType(givenEmailCertificationForm.verifyType().toString())
			.sendCount("1")
			.build();

		given(memberRepository.findByUsername(givenEmailCertificationForm.username()))
			.willReturn(Optional.empty());

		//when & then
		assertThatThrownBy(() -> authService.verify(givenEmailCertificationForm.username(),
			givenEmailCertificationForm.certificationCode(), givenEmailCertificationForm.verifyType()))
			.isInstanceOf(MemberException.class)
			.hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
	}

	@DisplayName("이메일 인증시 이미 인증이 되어 있을 때 실패 테스트")
	@Test
	void verify_already_certified_fail() {
		//given
		String givenRedisPrefix = "certification_email:";
		Address givenAddress = Address.builder()
			.city("testCity")
			.detail("testDetail")
			.country("testCountry")
			.district("testDistrict")
			.build();

		MemberDto givenMember = MemberDto.builder()
			.username("testEmail@naver.com")
			.nickname("testNickName")
			.password("!testPassword1234")
			.address(givenAddress)
			.memberStatus(MemberStatus.ACTIVE)
			.role(Role.ROLE_USER)
			.build();

		EmailCertificationForm givenEmailCertificationForm = EmailCertificationForm.builder()
			.certificationCode("testCode")
			.verifyType(VerifyType.SIGNUP)
			.username("testEmail@naver.com")
			.build();

		EmailCertification givenEmailCertification = EmailCertification.builder()
			.certificationCode(givenEmailCertificationForm.certificationCode())
			.verifyType(givenEmailCertificationForm.verifyType().toString())
			.sendCount("1")
			.build();

		given(memberRepository.findByUsername(givenEmailCertificationForm.username()))
			.willReturn(Optional.of(Member.from(givenMember)));

		//when & then
		assertThatThrownBy(() -> authService.verify(givenEmailCertificationForm.username(),
			givenEmailCertificationForm.certificationCode(), givenEmailCertificationForm.verifyType()))
			.isInstanceOf(AuthException.class)
			.hasMessage(AuthErrorCode.ALREADY_CERTIFIED.getMessage());
	}

	@Test
	@DisplayName("로그아웃 성공 시 리프레시 토큰 레디스에서 삭제")
	void logoutSuccess() {
		//given
		String accessToken = "accessToken";
		String username = "user@gmail.com";
		when(jwtUtils.getUsernameFromToken(any(String.class))).thenReturn(username);

		//when
		authService.logout(accessToken);

		//then
		verify(jwtUtils).getUsernameFromToken(accessToken);
		verify(refreshTokenService).deleteRefreshToken(username);
	}

	@DisplayName("이메일 인증 코드 발송 성공 테스트")
	@Test
	void send_success() {
		//given
		String givenUsername = "testEmail@naver.com";
		VerifyType givenVerifyType = VerifyType.PASSWORD_RESET;

		Address givenAddress = Address.builder()
			.city("testCity")
			.detail("testDetail")
			.country("testCountry")
			.district("testDistrict")
			.build();

		Member givenMember = Member.builder()
			.username(givenUsername)
			.nickname("testNickName")
			.password("!testPassword1234")
			.address(givenAddress)
			.memberStatus(MemberStatus.ACTIVE)
			.role(Role.ROLE_USER)
			.build();

		EmailCertification givenEmailCertification = EmailCertification.builder()
			.verifyType(VerifyType.PASSWORD_RESET.toString())
			.certificationCode("testCode")
			.sendCount("1")
			.build();

		Map<Object, Object> givenConvertMap = testObjectMapper.convertValue(givenEmailCertification, Map.class);

		doNothing().when(mailService).sendCertificationMail(any(String.class), any(EmailCertification.class), any(
			TemplateName.class));
		given(memberRepository.findByUsername(givenUsername)).willReturn(Optional.of(givenMember));

		//when
		authService.send(givenUsername, givenVerifyType);

		//then
		verify(mailService, times(1))
			.sendCertificationMail(any(String.class), any(EmailCertification.class), any(TemplateName.class));
		verify(memberRepository, times(1)).findByUsername(givenUsername);
	}

	@DisplayName("이메일 인증 코드 재발송시 인증 타입이 일치하지 않을 때 실패 테스트")
	@Test
	void send_verify_type_not_match_fail() {
		//given
		String givenUsername = "testEmail@naver.com";
		VerifyType givenVerifyType = VerifyType.PASSWORD_RESET;

		Address givenAddress = Address.builder()
			.city("testCity")
			.detail("testDetail")
			.country("testCountry")
			.district("testDistrict")
			.build();

		Member givenMember = Member.builder()
			.username(givenUsername)
			.nickname("testNickName")
			.password("!testPassword1234")
			.address(givenAddress)
			.memberStatus(MemberStatus.ACTIVE)
			.role(Role.ROLE_USER)
			.build();

		EmailCertification givenEmailCertification = EmailCertification.builder()
			.verifyType(VerifyType.SIGNUP.toString())
			.certificationCode("testCode")
			.sendCount("5")
			.build();

		Map<Object, Object> givenConvertMap = testObjectMapper.convertValue(givenEmailCertification, Map.class);

		given(memberRepository.findByUsername(givenUsername)).willReturn(Optional.of(givenMember));
		given(redisService.getHashDataAll("certification_email:" + givenUsername)).willReturn(givenConvertMap);
		given(objectMapper.convertValue(givenConvertMap, EmailCertification.class)).willReturn(givenEmailCertification);

		//when & then
		assertThatThrownBy(() -> authService.send(givenUsername, givenVerifyType))
			.isInstanceOf(AuthException.class)
			.hasMessage(AuthErrorCode.VERIFY_TYPE_NOT_MATCH.getMessage());
	}

	@DisplayName("이메일 인증 코드 재발송시 10분 이내 5회 이상 전송했을 때 실패 테스트")
	@Test
	void send_too_many_resend_attempts_fail() {
		//given
		String givenUsername = "testEmail@naver.com";
		VerifyType givenVerifyType = VerifyType.PASSWORD_RESET;

		Address givenAddress = Address.builder()
			.city("testCity")
			.detail("testDetail")
			.country("testCountry")
			.district("testDistrict")
			.build();

		Member givenMember = Member.builder()
			.username(givenUsername)
			.nickname("testNickName")
			.password("!testPassword1234")
			.address(givenAddress)
			.memberStatus(MemberStatus.ACTIVE)
			.role(Role.ROLE_USER)
			.build();

		EmailCertification givenEmailCertification = EmailCertification.builder()
			.verifyType(VerifyType.PASSWORD_RESET.toString())
			.certificationCode("testCode")
			.sendCount("5")
			.build();

		Map<Object, Object> givenConvertMap = testObjectMapper.convertValue(givenEmailCertification, Map.class);

		given(memberRepository.findByUsername(givenUsername)).willReturn(Optional.of(givenMember));
		given(redisService.getHashDataAll("certification_email:" + givenUsername)).willReturn(givenConvertMap);
		given(objectMapper.convertValue(givenConvertMap, EmailCertification.class)).willReturn(givenEmailCertification);

		//when & then
		assertThatThrownBy(() -> authService.send(givenUsername, givenVerifyType))
			.isInstanceOf(AuthException.class)
			.hasMessage(AuthErrorCode.TOO_MANY_RESEND_ATTEMPTS.getMessage());
	}
}
