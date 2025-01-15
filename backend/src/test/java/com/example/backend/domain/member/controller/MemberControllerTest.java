package com.example.backend.domain.member.controller;

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

import com.example.backend.domain.member.dto.MemberSignupRequest;
import com.example.backend.domain.member.service.MemberService;
import com.example.backend.global.config.CorsConfig;
import com.example.backend.global.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MemberController.class)
@Import({SecurityConfig.class, CorsConfig.class})
@Slf4j
class MemberControllerTest {
	@MockitoBean
	MemberService memberService;

	@Autowired
    private MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@DisplayName("회원가입 성공 테스트")
	@Test
	void signup_success() throws Exception {
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

		doNothing().when(memberService).signup(givenMemberSignupRequest.username(), givenMemberSignupRequest.nickname(),
			givenMemberSignupRequest.password(), givenMemberSignupRequest.verifyCode(), givenMemberSignupRequest.city(),
			givenMemberSignupRequest.district(), givenMemberSignupRequest.verifyCode(),
			givenMemberSignupRequest.detail());

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/members/join")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenMemberSignupRequest)));

		//then
		resultActions.andExpect(status().isCreated());
	}


}