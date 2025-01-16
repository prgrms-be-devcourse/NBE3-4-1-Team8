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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.backend.domain.common.VerifyType;
import com.example.backend.domain.member.exception.MemberErrorCode;
import com.example.backend.domain.member.exception.MemberException;
import com.example.backend.domain.member.repository.MemberRepository;
import com.example.backend.global.auth.dto.EmailCertificationForm;
import com.example.backend.global.auth.exception.AuthErrorCode;
import com.example.backend.global.auth.exception.AuthException;
import com.example.backend.global.auth.jwt.JwtProvider;
import com.example.backend.global.auth.service.AuthService;
import com.example.backend.global.auth.service.CustomUserDetailsService;
import com.example.backend.global.config.CorsConfig;
import com.example.backend.global.config.SecurityConfig;
import com.example.backend.global.exception.GlobalErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, CorsConfig.class})
@Slf4j
public class AuthControllerTest {
	@MockitoBean
	AuthService authService;

	@MockitoBean
	CustomUserDetailsService customUserDetailsService;

	@MockitoBean
	JwtProvider jwtProvider;

	@MockitoBean
	MemberRepository memberRepository;

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
			.andExpect(status().isNotFound())
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
}
