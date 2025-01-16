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
import com.example.backend.domain.member.repository.MemberRepository;
import com.example.backend.global.auth.dto.EmailCertificationForm;
import com.example.backend.global.auth.jwt.JwtProvider;
import com.example.backend.global.auth.service.AuthService;
import com.example.backend.global.auth.service.CustomUserDetailsService;
import com.example.backend.global.config.CorsConfig;
import com.example.backend.global.config.SecurityConfig;
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


}
