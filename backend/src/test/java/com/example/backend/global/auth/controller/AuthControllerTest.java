package com.example.backend.global.auth.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.backend.domain.common.VerifyType;
import com.example.backend.domain.member.exception.MemberErrorCode;
import com.example.backend.domain.member.exception.MemberException;
import com.example.backend.global.auth.dto.AuthForm;
import com.example.backend.global.auth.dto.AuthResponse;
import com.example.backend.global.auth.dto.EmailCertificationForm;
import com.example.backend.global.auth.dto.SendEmailCertificationCodeForm;
import com.example.backend.global.auth.exception.AuthErrorCode;
import com.example.backend.global.auth.exception.AuthException;
import com.example.backend.global.auth.service.AuthService;
import com.example.backend.global.auth.service.CookieService;
import com.example.backend.global.config.CorsConfig;
import com.example.backend.global.config.TestSecurityConfig;
import com.example.backend.global.exception.GlobalErrorCode;
import com.example.backend.global.exception.GlobalException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
@Import({TestSecurityConfig.class, CorsConfig.class})
@Slf4j
public class AuthControllerTest {
	@MockitoBean
	AuthService authService;

	@MockitoBean
	CookieService cookieService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@DisplayName("이메일 인증 성공 테스트")
	@Test
	void verify_success() throws Exception {
		//given
		EmailCertificationForm givenEmailCertificationForm = EmailCertificationForm.builder()
			.username("testEmail@naver.com")
			.certificationCode("testCode")
			.verifyType(VerifyType.SIGNUP)
			.build();

		doNothing().when(authService).verify(givenEmailCertificationForm.username(),
			givenEmailCertificationForm.certificationCode(), givenEmailCertificationForm.verifyType());

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/verify")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenEmailCertificationForm)));

		//then
		resultActions.andExpect(status().isOk());
	}

	@DisplayName("이메일 인증 정보 조회 실패 테스트")
	@Test
	void verify_certification_not_found_fail() throws Exception {
		//given
		EmailCertificationForm givenEmailCertificationForm = EmailCertificationForm.builder()
			.username("testEmail@naver.com")
			.certificationCode("testCode")
			.verifyType(VerifyType.SIGNUP)
			.build();

		doThrow(new AuthException(AuthErrorCode.CERTIFICATION_CODE_NOT_FOUND))
			.when(authService).verify(givenEmailCertificationForm.username(),
				givenEmailCertificationForm.certificationCode(), givenEmailCertificationForm.verifyType());

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/verify")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenEmailCertificationForm)));

		//then
		resultActions
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(AuthErrorCode.CERTIFICATION_CODE_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(AuthErrorCode.CERTIFICATION_CODE_NOT_FOUND.getMessage()));
	}

	@DisplayName("이메일 인증 코드 일치하지 않을 때 실패 테스트")
	@Test
	void verify_certification_not_match_fail() throws Exception {
		//given
		EmailCertificationForm givenEmailCertificationForm = EmailCertificationForm.builder()
			.username("testEmail@naver.com")
			.certificationCode("testCode")
			.verifyType(VerifyType.SIGNUP)
			.build();

		doThrow(new AuthException(AuthErrorCode.CERTIFICATION_CODE_NOT_MATCH))
			.when(authService).verify(givenEmailCertificationForm.username(),
				givenEmailCertificationForm.certificationCode(), givenEmailCertificationForm.verifyType());

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/verify")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenEmailCertificationForm)));

		//then
		resultActions
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value(AuthErrorCode.CERTIFICATION_CODE_NOT_MATCH.getCode()))
			.andExpect(jsonPath("$.message").value(AuthErrorCode.CERTIFICATION_CODE_NOT_MATCH.getMessage()));
	}

	@DisplayName("이메일 인증 타입 일치하지 않을 때 실패 테스트")
	@Test
	void verify_verify_type_not_match_fail() throws Exception {
		//given
		EmailCertificationForm givenEmailCertificationForm = EmailCertificationForm.builder()
			.username("testEmail@naver.com")
			.certificationCode("testCode")
			.verifyType(VerifyType.SIGNUP)
			.build();

		doThrow(new AuthException(AuthErrorCode.VERIFY_TYPE_NOT_MATCH))
			.when(authService).verify(givenEmailCertificationForm.username(),
				givenEmailCertificationForm.certificationCode(), givenEmailCertificationForm.verifyType());

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/verify")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenEmailCertificationForm)));

		//then
		resultActions
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value(AuthErrorCode.VERIFY_TYPE_NOT_MATCH.getCode()))
			.andExpect(jsonPath("$.message").value(AuthErrorCode.VERIFY_TYPE_NOT_MATCH.getMessage()));
	}

	@DisplayName("이메일 인증시 회원이 존재하지 않을 때 실패 테스트")
	@Test
	void verify_member_not_found_fail() throws Exception {
		//given
		EmailCertificationForm givenEmailCertificationForm = EmailCertificationForm.builder()
			.username("testEmail@naver.com")
			.certificationCode("testCode")
			.verifyType(VerifyType.SIGNUP)
			.build();

		doThrow(new MemberException(MemberErrorCode.MEMBER_NOT_FOUND))
			.when(authService).verify(givenEmailCertificationForm.username(),
				givenEmailCertificationForm.certificationCode(), givenEmailCertificationForm.verifyType());

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/verify")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenEmailCertificationForm)));

		//then
		resultActions
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(MemberErrorCode.MEMBER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(MemberErrorCode.MEMBER_NOT_FOUND.getMessage()));
	}

	@DisplayName("이메일 인증시 이미 인증이 되어 있을 때 실패 테스트")
	@Test
	void verify_already_certified_fail() throws Exception {
		//given
		EmailCertificationForm givenEmailCertificationForm = EmailCertificationForm.builder()
			.username("testEmail@naver.com")
			.certificationCode("testCode")
			.verifyType(VerifyType.SIGNUP)
			.build();

		doThrow(new AuthException(AuthErrorCode.ALREADY_CERTIFIED))
			.when(authService).verify(givenEmailCertificationForm.username(),
				givenEmailCertificationForm.certificationCode(), givenEmailCertificationForm.verifyType());

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/verify")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenEmailCertificationForm)));

		//then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(AuthErrorCode.ALREADY_CERTIFIED.getCode()))
			.andExpect(jsonPath("$.message").value(AuthErrorCode.ALREADY_CERTIFIED.getMessage()));
	}

	@DisplayName("이메일 인증시 이메일 형식이 틀렸을 때 실패 테스트")
	@Test
	void verify_email_not_pattern_fail() throws Exception {
		//given
		EmailCertificationForm givenEmailCertificationForm = EmailCertificationForm.builder()
			.username("testEmailnaver.com")
			.certificationCode("testCode")
			.verifyType(VerifyType.SIGNUP)
			.build();

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/verify")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenEmailCertificationForm)));

		//then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_VALID.getMessage()))
			.andExpect(jsonPath("$.errorDetails[0].field").value("username"))
			.andExpect(jsonPath("$.errorDetails[0].reason").value("유효하지 않은 이메일 입니다."));
	}

	@DisplayName("이메일 인증시 인증 코드가 비었을 때 실패 테스트")
	@Test
	void verify_code_not_blank_fail() throws Exception {
		//given
		EmailCertificationForm givenEmailCertificationForm = EmailCertificationForm.builder()
			.username("testEmail@naver.com")
			.certificationCode("")
			.verifyType(VerifyType.SIGNUP)
			.build();

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/verify")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenEmailCertificationForm)));

		//then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_VALID.getMessage()))
			.andExpect(jsonPath("$.errorDetails[0].field").value("certificationCode"))
			.andExpect(jsonPath("$.errorDetails[0].reason").value("인증 코드는 필수 항목 입니다."));
	}

	@DisplayName("이메일 인증시 인증 타입이 비었을 때 실패 테스트")
	@Test
	void verify_verify_type_valid_enum_fail() throws Exception {
		//given
		EmailCertificationForm givenEmailCertificationForm = EmailCertificationForm.builder()
			.username("testEmail@naver.com")
			.certificationCode("testCode")
			.verifyType(null)
			.build();

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/verify")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenEmailCertificationForm)));

		//then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_VALID.getMessage()))
			.andExpect(jsonPath("$.errorDetails[0].field").value("verifyType"))
			.andExpect(jsonPath("$.errorDetails[0].reason").value("인증 타입은 필수 항목 입니다."));
	}

	@Test
	@DisplayName("로그인 성공")
	void login_success() throws Exception {
		//given
		AuthForm authForm = AuthForm.builder()
			.username("user@gmail.com")
			.password("Password123!")
			.build();

		AuthResponse authResponse = AuthResponse.of("user@gmail.com", "accessToken", "refreshToken");
		when(authService.login(any(AuthForm.class))).thenReturn(authResponse);
		doNothing().when(cookieService).addAccessTokenToCookie(any(String.class), any(HttpServletResponse.class));
		doNothing().when(cookieService).addRefreshTokenToCookie(any(String.class), any(HttpServletResponse.class));

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/login")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(authForm)));

		//then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.message").value("로그인 성공"))
			.andExpect(jsonPath("$.data.username").value("user@gmail.com"));

		verify(authService, times(1)).login(any(AuthForm.class));
		verify(cookieService, times(1)).addAccessTokenToCookie(eq("accessToken"),
			any(HttpServletResponse.class));
		verify(cookieService, times(1)).addRefreshTokenToCookie(eq("refreshToken"),
			any(HttpServletResponse.class));
	}

	@Test
	@DisplayName("로그인 실패 - 입력한 이메일 유저가 존재하지 않을 경우")
	void login_fail_member_not_found() throws Exception {
		// given
		AuthForm authForm = new AuthForm("user@gmail.com", "Password123!");

		when(authService.login(any(AuthForm.class))).thenThrow(new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/login")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(authForm)));

		// then
		resultActions
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(AuthErrorCode.MEMBER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(AuthErrorCode.MEMBER_NOT_FOUND.getMessage()));

		verify(authService, times(1)).login(any(AuthForm.class));
		verify(cookieService, times(0)).addAccessTokenToCookie(any(String.class), any(HttpServletResponse.class));
		verify(cookieService, times(0)).addRefreshTokenToCookie(any(String.class), any(HttpServletResponse.class));
	}

	@Test
	@DisplayName("로그인 실패 - 비밀번호가 일치하지 않는 경우")
	void login_fail_password_not_match() throws Exception {
		// given
		AuthForm authForm = new AuthForm("user@gmail.com", "Password123!");

		when(authService.login(any(AuthForm.class))).thenThrow(new AuthException(AuthErrorCode.PASSWORD_NOT_MATCH));

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/login")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(authForm)));

		// then
		resultActions
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value(AuthErrorCode.PASSWORD_NOT_MATCH.getCode()))
			.andExpect(jsonPath("$.message").value(AuthErrorCode.PASSWORD_NOT_MATCH.getMessage()));

		verify(authService, times(1)).login(any(AuthForm.class));
		verify(cookieService, times(0)).addAccessTokenToCookie(any(String.class), any(HttpServletResponse.class));
		verify(cookieService, times(0)).addRefreshTokenToCookie(any(String.class), any(HttpServletResponse.class));
	}

	@Test
	@DisplayName("로그인 실패 - 이메일이 빈 값일 경우")
	void login_fail_email_empty() throws Exception {
		// given
		AuthForm authForm = new AuthForm("", "Password123!");

		when(authService.login(any(AuthForm.class))).thenThrow(new GlobalException(GlobalErrorCode.NOT_VALID));

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/login")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(authForm)));

		// then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_VALID.getMessage()))
			.andExpect(jsonPath("$.errorDetails[0].field").value("username"))
			.andExpect(jsonPath("$.errorDetails[0].reason").value("유효하지 않은 이메일 입니다."));

		verify(authService, times(0)).login(any(AuthForm.class));
		verify(cookieService, times(0)).addAccessTokenToCookie(any(String.class), any(HttpServletResponse.class));
		verify(cookieService, times(0)).addRefreshTokenToCookie(any(String.class), any(HttpServletResponse.class));
	}

	@Test
	@DisplayName("로그인 실패 - 이메일 형식이 아닐 경우")
	void login_fail_not_email() throws Exception {
		// given
		AuthForm authForm = new AuthForm("", "Password123!");

		when(authService.login(any(AuthForm.class))).thenThrow(new GlobalException(GlobalErrorCode.NOT_VALID));

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/login")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(authForm)));

		// then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_VALID.getMessage()))
			.andExpect(jsonPath("$.errorDetails[0].field").value("username"))
			.andExpect(jsonPath("$.errorDetails[0].reason").value("유효하지 않은 이메일 입니다."));

		verify(authService, times(0)).login(any(AuthForm.class));
		verify(cookieService, times(0)).addAccessTokenToCookie(any(String.class), any(HttpServletResponse.class));
		verify(cookieService, times(0)).addRefreshTokenToCookie(any(String.class), any(HttpServletResponse.class));
	}

	@Test
	@DisplayName("로그아웃 성공 테스트")
	@WithMockUser
	void logout_success() throws Exception {
		// given
		String accessToken = "accessToken";
		when(cookieService.getAccessTokenFromRequest(any(HttpServletRequest.class))).thenReturn(accessToken);
		doNothing().when(authService).logout(accessToken);
		doNothing().when(cookieService).deleteRefreshTokenFromCookie(any(HttpServletResponse.class));

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/logout")
			.cookie(new Cookie("accessToken", accessToken)));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.message").value("로그아웃 성공"))
			.andExpect(jsonPath("$.data").isEmpty());

		verify(cookieService, times(1)).getAccessTokenFromRequest(any(HttpServletRequest.class));
		verify(authService, times(1)).logout(accessToken);
		verify(cookieService, times(1)).deleteRefreshTokenFromCookie(any(HttpServletResponse.class));
	}

	@DisplayName("인증 코드 이메일 전송 성공 테스트")
	@Test
	void send_success() throws Exception {
	    //given
		SendEmailCertificationCodeForm givenSendEmailCertificationCodeForm = SendEmailCertificationCodeForm.builder()
			.username("testEmail@naver.com")
			.verifyType(VerifyType.SIGNUP)
			.build();

		doNothing().when(authService)
			.send(givenSendEmailCertificationCodeForm.username(), givenSendEmailCertificationCodeForm.verifyType());

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/code")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenSendEmailCertificationCodeForm)));

	    //then
		resultActions
			.andExpect(status().isOk());
	}

	@DisplayName("인증 타입이 일치하지 않을 때 실패 테스트")
	@Test
	void send_verify_type_not_match_fail() throws Exception {
	    //given
		SendEmailCertificationCodeForm givenSendEmailCertificationCodeForm = SendEmailCertificationCodeForm.builder()
			.username("testEmail@naver.com")
			.verifyType(VerifyType.SIGNUP)
			.build();

		doThrow(new AuthException(AuthErrorCode.VERIFY_TYPE_NOT_MATCH))
			.when(authService)
			.send(givenSendEmailCertificationCodeForm.username(), givenSendEmailCertificationCodeForm.verifyType());

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/code")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenSendEmailCertificationCodeForm)));

	    //then
		resultActions
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value(AuthErrorCode.VERIFY_TYPE_NOT_MATCH.getCode()))
			.andExpect(jsonPath("$.message").value(AuthErrorCode.VERIFY_TYPE_NOT_MATCH.getMessage()));
	}
}
