package com.example.backend.domain.member.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.backend.global.config.TestSecurityConfig;
import com.example.backend.global.config.CorsConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.backend.domain.member.dto.MemberSignupForm;
import com.example.backend.domain.member.exception.MemberErrorCode;
import com.example.backend.domain.member.exception.MemberException;
import com.example.backend.domain.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@WebMvcTest(MemberController.class)
@Import({TestSecurityConfig.class, CorsConfig.class})
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

		doNothing().when(memberService).signup(givenMemberSignupForm.username(), givenMemberSignupForm.nickname(),
			givenMemberSignupForm.password(), givenMemberSignupForm.city(),
			givenMemberSignupForm.district(), givenMemberSignupForm.country(), givenMemberSignupForm.detail());

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/members/join")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenMemberSignupForm)));

		//then
		resultActions.andExpect(status().isCreated());
	}

@DisplayName("회원가입 이메일 유효성 검사 실패 테스트")
	@Test
	void signup_username_valid_username_fail() throws Exception {
		//given
		MemberSignupForm givenMemberSignupForm = MemberSignupForm.builder()
			.username("test")
			.nickname("testNickName")
			.password("!testPassword1234")
			.passwordCheck("!testPassword1234")
			.city("testCity")
			.detail("testDetail")
			.country("testCountry")
			.district("testDistrict")
			.build();

		doNothing().when(memberService).signup(givenMemberSignupForm.username(), givenMemberSignupForm.nickname(),
			givenMemberSignupForm.password(), givenMemberSignupForm.city(),
			givenMemberSignupForm.district(), givenMemberSignupForm.country(),
			givenMemberSignupForm.detail());

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/members/join")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenMemberSignupForm)));

		//then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("400-1"))
			.andExpect(jsonPath("$.errorDetails[0].field").value("username"))
			.andExpect(jsonPath("$.errorDetails[0].reason").value("유효하지 않은 이메일 입니다."));
	}

	@DisplayName("회원가입 닉네임 유효성 검사 실패 테스트")
	@Test
	void signup_nickname_valid_nickname_fail() throws Exception {
		//given
		MemberSignupForm givenMemberSignupForm = MemberSignupForm.builder()
			.username("test@naver.com")
			.nickname("")
			.password("!testPassword1234")
			.passwordCheck("!testPassword1234")
			.city("testCity")
			.detail("testDetail")
			.country("testCountry")
			.district("testDistrict")
			.build();

		doNothing().when(memberService).signup(givenMemberSignupForm.username(), givenMemberSignupForm.nickname(),
			givenMemberSignupForm.password(), givenMemberSignupForm.city(),
			givenMemberSignupForm.district(), givenMemberSignupForm.country(),
			givenMemberSignupForm.detail());

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/members/join")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenMemberSignupForm)));

		//then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("400-1"))
			.andExpect(jsonPath("$.errorDetails[0].field").value("nickname"))
			.andExpect(jsonPath("$.errorDetails[0].reason").value("유효하지 않은 회원 이름 입니다."));
	}

	@DisplayName("회원가입 비밀번호 유효성 검사 실패 테스트")
	@Test
	void signup_password_valid_password_fail() throws Exception {
		//given
		MemberSignupForm givenMemberSignupForm = MemberSignupForm.builder()
			.username("test@naver.com")
			.nickname("testNickname")
			.password("testPassword1234")
			.passwordCheck("!testPassword1234")
			.city("testCity")
			.detail("testDetail")
			.country("testCountry")
			.district("testDistrict")
			.build();

		doNothing().when(memberService).signup(givenMemberSignupForm.username(), givenMemberSignupForm.nickname(),
			givenMemberSignupForm.password(), givenMemberSignupForm.city(),
			givenMemberSignupForm.district(), givenMemberSignupForm.country(),
			givenMemberSignupForm.detail());

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/members/join")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenMemberSignupForm)));

		//then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("400-1"))
			.andExpect(jsonPath("$.errorDetails[0].field").value("password"))
			.andExpect(jsonPath("$.errorDetails[0].reason")
				.value("공백 없이 비밀번호는 최소 8자리, 최대 20자리이며 대소문자, 숫자, 특수문자 1개씩 필수 입력해야 합니다."));
	}

	@DisplayName("회원가입 비밀번호 매치 실패 테스트")
	@Test
	void signup_password_match_fail() throws Exception {
		//given
		MemberSignupForm givenMemberSignupForm = MemberSignupForm.builder()
			.username("test@naver.com")
			.nickname("testNickname")
			.password("!testPassword1234")
			.passwordCheck("!testPassword124")
			.city("testCity")
			.detail("testDetail")
			.country("testCountry")
			.district("testDistrict")
			.build();

		doNothing().when(memberService).signup(givenMemberSignupForm.username(), givenMemberSignupForm.nickname(),
			givenMemberSignupForm.password(), givenMemberSignupForm.city(),
			givenMemberSignupForm.district(), givenMemberSignupForm.country(),
			givenMemberSignupForm.detail());

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/members/join")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenMemberSignupForm)));

		//then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("400-1"))
			.andExpect(jsonPath("$.errorDetails[0].field").value("memberSignupForm"))
			.andExpect(jsonPath("$.errorDetails[0].reason")
				.value("비밀번호와 비밀번호 확인이 일치하지 않습니다."));
	}

	@DisplayName("회원가입 도시 유효성 검사 실패 테스트")
	@Test
	void signup_city_not_blank_fail() throws Exception {
		//given
		MemberSignupForm givenMemberSignupForm = MemberSignupForm.builder()
			.username("test@naver.com")
			.nickname("testNickname")
			.password("!testPassword1234")
			.passwordCheck("!testPassword1234")
			.city("")
			.detail("testDetail")
			.country("testCountry")
			.district("testDistrict")
			.build();

		doNothing().when(memberService).signup(givenMemberSignupForm.username(), givenMemberSignupForm.nickname(),
			givenMemberSignupForm.password(), givenMemberSignupForm.city(),
			givenMemberSignupForm.district(), givenMemberSignupForm.country(),
			givenMemberSignupForm.detail());

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/members/join")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenMemberSignupForm)));

		//then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("400-1"))
			.andExpect(jsonPath("$.errorDetails[0].field").value("city"))
			.andExpect(jsonPath("$.errorDetails[0].reason")
				.value("도시는 필수 항목 입니다."));
	}

	@DisplayName("회원가입 상세주소 유효성 검사 실패 테스트")
	@Test
	void signup_detail_not_blank_fail() throws Exception {
		//given
		MemberSignupForm givenMemberSignupForm = MemberSignupForm.builder()
			.username("test@naver.com")
			.nickname("testNickname")
			.password("!testPassword1234")
			.passwordCheck("!testPassword1234")
			.city("testCity")
			.detail("")
			.country("testCountry")
			.district("testDistrict")
			.build();

		doNothing().when(memberService).signup(givenMemberSignupForm.username(), givenMemberSignupForm.nickname(),
			givenMemberSignupForm.password(), givenMemberSignupForm.city(),
			givenMemberSignupForm.district(), givenMemberSignupForm.country(),
			givenMemberSignupForm.detail());

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/members/join")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenMemberSignupForm)));

		//then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("400-1"))
			.andExpect(jsonPath("$.errorDetails[0].field").value("detail"))
			.andExpect(jsonPath("$.errorDetails[0].reason")
				.value("상세 주소는 필수 항목 입니다."));
	}

	@DisplayName("회원가입 도로명 주소 유효성 검사 실패 테스트")
	@Test
	void signup_country_not_blank_fail() throws Exception {
		//given
		MemberSignupForm givenMemberSignupForm = MemberSignupForm.builder()
			.username("test@naver.com")
			.nickname("testNickname")
			.password("!testPassword1234")
			.passwordCheck("!testPassword1234")
			.city("testCity")
			.detail("testDetail")
			.country("")
			.district("testDistrict")
			.build();

		doNothing().when(memberService).signup(givenMemberSignupForm.username(), givenMemberSignupForm.nickname(),
			givenMemberSignupForm.password(), givenMemberSignupForm.city(),
			givenMemberSignupForm.district(), givenMemberSignupForm.country(),
			givenMemberSignupForm.detail());

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/members/join")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenMemberSignupForm)));

		//then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("400-1"))
			.andExpect(jsonPath("$.errorDetails[0].field").value("country"))
			.andExpect(jsonPath("$.errorDetails[0].reason")
				.value("도로명 주소는 필수 항목 입니다."));
	}

	@DisplayName("회원가입 지역 구 유효성 검사 실패 테스트")
	@Test
	void signup_district_not_blank_fail() throws Exception {
		//given
		MemberSignupForm givenMemberSignupForm = MemberSignupForm.builder()
			.username("test@naver.com")
			.nickname("testNickname")
			.password("!testPassword1234")
			.passwordCheck("!testPassword1234")
			.city("testCity")
			.detail("testDetail")
			.country("testCountry")
			.district("")
			.build();

		doNothing().when(memberService).signup(givenMemberSignupForm.username(), givenMemberSignupForm.nickname(),
			givenMemberSignupForm.password(), givenMemberSignupForm.city(),
			givenMemberSignupForm.district(), givenMemberSignupForm.country(),
			givenMemberSignupForm.detail());

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/members/join")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenMemberSignupForm)));

		//then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("400-1"))
			.andExpect(jsonPath("$.errorDetails[0].field").value("district"))
			.andExpect(jsonPath("$.errorDetails[0].reason")
				.value("지역 구는 필수 항목 입니다."));
	}

	@DisplayName("회원가입시 닉네임 중복 검사 실패 테스트")
	@Test
	void signup_nickname_exists_fail() throws Exception {
		//given
		MemberSignupForm givenMemberSignupForm = MemberSignupForm.builder()
			.username("test@naver.com")
			.nickname("testNickname")
			.password("!testPassword1234")
			.passwordCheck("!testPassword1234")
			.city("testCity")
			.detail("testDetail")
			.country("testCountry")
			.district("testDistrict")
			.build();


		doThrow(new MemberException(MemberErrorCode.EXISTS_NICKNAME))
			.when(memberService).signup(any(String.class), any(String.class),
			any(String.class), any(String.class), any(String.class),
			any(String.class), any(String.class));

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/members/join")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenMemberSignupForm)));

		//then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("400-2"))
			.andExpect(jsonPath("$.message").value("중복된 닉네임 입니다."));
	}

	@DisplayName("회원가입시 이메일 중복 검사 실패 테스트")
	@Test
	void signup_username_exists_fail() throws Exception {
		//given
		MemberSignupForm givenMemberSignupForm = MemberSignupForm.builder()
			.username("test@naver.com")
			.nickname("testNickname")
			.password("!testPassword1234")
			.passwordCheck("!testPassword1234")
			.city("testCity")
			.detail("testDetail")
			.country("testCountry")
			.district("testDistrict")
			.build();


		doThrow(new MemberException(MemberErrorCode.EXISTS_USERNAME))
			.when(memberService).signup(any(String.class), any(String.class),
			any(String.class), any(String.class), any(String.class),
			any(String.class), any(String.class));

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/members/join")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenMemberSignupForm)));

		//then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("400-1"))
			.andExpect(jsonPath("$.message").value("중복된 이메일 입니다."));
	}
}