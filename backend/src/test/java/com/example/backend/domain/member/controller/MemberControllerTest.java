package com.example.backend.domain.member.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.backend.domain.common.Address;
import com.example.backend.domain.member.conveter.MemberConverter;
import com.example.backend.domain.member.dto.MemberDto;
import com.example.backend.domain.member.dto.MemberInfoResponse;
import com.example.backend.domain.member.dto.MemberModifyForm;
import com.example.backend.domain.member.dto.MemberSignupForm;
import com.example.backend.domain.member.dto.PasswordChangeForm;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.entity.MemberStatus;
import com.example.backend.domain.member.entity.Role;
import com.example.backend.domain.member.exception.MemberErrorCode;
import com.example.backend.domain.member.exception.MemberException;
import com.example.backend.domain.member.service.MemberDeleteService;
import com.example.backend.domain.member.service.MemberService;
import com.example.backend.global.auth.model.CustomUserDetails;
import com.example.backend.global.auth.service.CookieService;
import com.example.backend.global.config.CorsConfig;
import com.example.backend.global.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@WebMvcTest(MemberController.class)
@Import({TestSecurityConfig.class, CorsConfig.class})
@Slf4j
class MemberControllerTest {
	@MockitoBean
	MemberService memberService;

	@MockitoBean
	MemberDeleteService memberDeleteService;

	@MockitoBean
	CookieService cookieService;

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

	@Test
	@DisplayName("회원 정보 조회")
	void getMemberInfo() throws Exception {
		// given
		Address address = Address.builder()
			.city("testCity")
			.district("testDistrict")
			.country("testCountry")
			.detail("testDetail")
			.build();
		Member member = Member.builder()
			.username("test@naver.com")
			.nickname("testNickname")
			.memberStatus(MemberStatus.ACTIVE)
			.role(Role.ROLE_USER)
			.address(address)
			.build();
		CustomUserDetails customUserDetails = new CustomUserDetails(member);

		// Authentication 설정
		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/members")
			.with(SecurityMockMvcRequestPostProcessors.authentication(authenticationToken)));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.username").value("test@naver.com"))
			.andExpect(jsonPath("$.data.nickname").value("testNickname"))
			.andExpect(jsonPath("$.data.address.city").value("testCity"))
			.andExpect(jsonPath("$.data.address.district").value("testDistrict"))
			.andExpect(jsonPath("$.data.address.country").value("testCountry"))
			.andExpect(jsonPath("$.data.address.detail").value("testDetail"))
			.andExpect(jsonPath("$.success").value(true));

	}

	@Test
	@DisplayName("회원 정보 수정 성공")
	void modify_success() throws Exception {
		// given
		Address address = Address.builder()
			.city("testCity")
			.district("testDistrict")
			.country("testCountry")
			.detail("testDetail")
			.build();
		Member member = Member.builder()
			.username("test@naver.com")
			.nickname("testNickname")
			.memberStatus(MemberStatus.ACTIVE)
			.role(Role.ROLE_USER)
			.address(address)
			.build();
		CustomUserDetails customUserDetails = new CustomUserDetails(member);

		// Authentication 설정
		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

		MemberModifyForm memberModifyForm = MemberModifyForm.builder()
			.nickname("updatedNickname")
			.city("updatedCity")
			.district("updatedDistrict")
			.country("updatedCountry")
			.detail("updatedDetail")
			.build();

		Member updatedMember = MemberConverter.of(member.toModel(), memberModifyForm);
		MemberInfoResponse response = MemberConverter.from(updatedMember);
		when(memberService.modify(any(MemberDto.class), any(MemberModifyForm.class))).thenReturn(response);

		// when
		ResultActions resultActions = mockMvc.perform(patch("/api/v1/members")
			.with(SecurityMockMvcRequestPostProcessors.authentication(authenticationToken))
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(memberModifyForm)));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.username").value("test@naver.com"))
			.andExpect(jsonPath("$.data.nickname").value("updatedNickname"))
			.andExpect(jsonPath("$.data.address.city").value("updatedCity"))
			.andExpect(jsonPath("$.data.address.district").value("updatedDistrict"))
			.andExpect(jsonPath("$.data.address.country").value("updatedCountry"))
			.andExpect(jsonPath("$.data.address.detail").value("updatedDetail"))
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@DisplayName("회원 정보 수정 실패 - 중복된 닉네임일 경우")
	void modify_fail_nickname_already_exists() throws Exception {
		// given
		Address address = Address.builder()
			.city("testCity")
			.district("testDistrict")
			.country("testCountry")
			.detail("testDetail")
			.build();
		Member member = Member.builder()
			.username("test@naver.com")
			.nickname("testNickname")
			.memberStatus(MemberStatus.ACTIVE)
			.role(Role.ROLE_USER)
			.address(address)
			.build();
		CustomUserDetails customUserDetails = new CustomUserDetails(member);

		// Authentication 설정
		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

		MemberModifyForm memberModifyForm = MemberModifyForm.builder()
			.nickname("duplicateNickname")
			.city("updatedCity")
			.district("updatedDistrict")
			.country("updatedCountry")
			.detail("updatedDetail")
			.build();

		doThrow(new MemberException(MemberErrorCode.EXISTS_NICKNAME))
			.when(memberService)
			.modify(any(MemberDto.class), any(MemberModifyForm.class));

		// when
		ResultActions resultActions = mockMvc.perform(patch("/api/v1/members")
			.with(SecurityMockMvcRequestPostProcessors.authentication(authenticationToken))
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(memberModifyForm)));

		// then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("400-2"))
			.andExpect(jsonPath("$.message").value("중복된 닉네임 입니다."));
	}

	@Test
	@DisplayName("회원 정보 수정 실패 - 닉네임 유효성 검사 실패")
	void modify_fail_nickname_not_valid() throws Exception {
		// given
		Address address = Address.builder()
			.city("testCity")
			.district("testDistrict")
			.country("testCountry")
			.detail("testDetail")
			.build();
		Member member = Member.builder()
			.username("test@naver.com")
			.nickname("testNickname")
			.memberStatus(MemberStatus.ACTIVE)
			.role(Role.ROLE_USER)
			.address(address)
			.build();
		CustomUserDetails customUserDetails = new CustomUserDetails(member);

		// Authentication 설정
		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

		MemberModifyForm memberModifyForm = MemberModifyForm.builder()
			.nickname("")
			.city("updatedCity")
			.district("updatedDistrict")
			.country("updatedCountry")
			.detail("updatedDetail")
			.build();

		// when
		ResultActions resultActions = mockMvc.perform(patch("/api/v1/members")
			.with(SecurityMockMvcRequestPostProcessors.authentication(authenticationToken))
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(memberModifyForm)));

		// then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("400-1"))
			.andExpect(jsonPath("$.errorDetails[0].field").value("nickname"))
			.andExpect(jsonPath("$.errorDetails[0].reason").value("유효하지 않은 회원 이름 입니다."));
	}

	@Test
	@DisplayName("회원 정보 수정 실패 - 도시 유효성 검사 실패")
	void modify_fail_city_not_valid() throws Exception {
		// given
		Address address = Address.builder()
			.city("testCity")
			.district("testDistrict")
			.country("testCountry")
			.detail("testDetail")
			.build();
		Member member = Member.builder()
			.username("test@naver.com")
			.nickname("testNickname")
			.memberStatus(MemberStatus.ACTIVE)
			.role(Role.ROLE_USER)
			.address(address)
			.build();
		CustomUserDetails customUserDetails = new CustomUserDetails(member);

		// Authentication 설정
		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

		MemberModifyForm memberModifyForm = MemberModifyForm.builder()
			.nickname("updatedNickname")
			.city("")
			.district("updatedDistrict")
			.country("updatedCountry")
			.detail("updatedDetail")
			.build();

		// when
		ResultActions resultActions = mockMvc.perform(patch("/api/v1/members")
			.with(SecurityMockMvcRequestPostProcessors.authentication(authenticationToken))
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(memberModifyForm)));

		// then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("400-1"))
			.andExpect(jsonPath("$.errorDetails[0].field").value("city"))
			.andExpect(jsonPath("$.errorDetails[0].reason").value("도시는 필수 항목 입니다."));
	}

	@Test
	@DisplayName("회원 정보 수정 실패 - 지역 구 유효성 검사 실패")
	void modify_fail_district_not_valid() throws Exception {
		// given
		Address address = Address.builder()
			.city("testCity")
			.district("testDistrict")
			.country("testCountry")
			.detail("testDetail")
			.build();
		Member member = Member.builder()
			.username("test@naver.com")
			.nickname("testNickname")
			.memberStatus(MemberStatus.ACTIVE)
			.role(Role.ROLE_USER)
			.address(address)
			.build();
		CustomUserDetails customUserDetails = new CustomUserDetails(member);

		// Authentication 설정
		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

		MemberModifyForm memberModifyForm = MemberModifyForm.builder()
			.nickname("updatedNickname")
			.city("updatedCity")
			.district("")
			.country("updatedCountry")
			.detail("updatedDetail")
			.build();

		// when
		ResultActions resultActions = mockMvc.perform(patch("/api/v1/members")
			.with(SecurityMockMvcRequestPostProcessors.authentication(authenticationToken))
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(memberModifyForm)));

		// then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("400-1"))
			.andExpect(jsonPath("$.errorDetails[0].field").value("district"))
			.andExpect(jsonPath("$.errorDetails[0].reason").value("지역 구는 필수 항목 입니다."));
	}

	@Test
	@DisplayName("회원 정보 수정 실패 - 도로명 주소 유효성 검사 실패")
	void modify_fail_country_not_valid() throws Exception {
		// given
		Address address = Address.builder()
			.city("testCity")
			.district("testDistrict")
			.country("testCountry")
			.detail("testDetail")
			.build();
		Member member = Member.builder()
			.username("test@naver.com")
			.nickname("testNickname")
			.memberStatus(MemberStatus.ACTIVE)
			.role(Role.ROLE_USER)
			.address(address)
			.build();
		CustomUserDetails customUserDetails = new CustomUserDetails(member);

		// Authentication 설정
		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

		MemberModifyForm memberModifyForm = MemberModifyForm.builder()
			.nickname("updatedNickname")
			.city("updatedCity")
			.district("updatedDistrict")
			.country("")
			.detail("updatedDetail")
			.build();

		// when
		ResultActions resultActions = mockMvc.perform(patch("/api/v1/members")
			.with(SecurityMockMvcRequestPostProcessors.authentication(authenticationToken))
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(memberModifyForm)));

		// then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("400-1"))
			.andExpect(jsonPath("$.errorDetails[0].field").value("country"))
			.andExpect(jsonPath("$.errorDetails[0].reason").value("도로명 주소는 필수 항목 입니다."));
	}

	@Test
	@DisplayName("회원 정보 수정 실패 - 상세 주소 유효성 검사 실패")
	void modify_fail_detail_not_valid() throws Exception {
		// given
		Address address = Address.builder()
			.city("testCity")
			.district("testDistrict")
			.country("testCountry")
			.detail("testDetail")
			.build();
		Member member = Member.builder()
			.username("test@naver.com")
			.nickname("testNickname")
			.memberStatus(MemberStatus.ACTIVE)
			.role(Role.ROLE_USER)
			.address(address)
			.build();
		CustomUserDetails customUserDetails = new CustomUserDetails(member);

		// Authentication 설정
		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

		MemberModifyForm memberModifyForm = MemberModifyForm.builder()
			.nickname("updatedNickname")
			.city("updatedCity")
			.district("updatedDistrict")
			.country("updatedCountry")
			.detail("")
			.build();

		// when
		ResultActions resultActions = mockMvc.perform(patch("/api/v1/members")
			.with(SecurityMockMvcRequestPostProcessors.authentication(authenticationToken))
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(memberModifyForm)));

		// then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("400-1"))
			.andExpect(jsonPath("$.errorDetails[0].field").value("detail"))
			.andExpect(jsonPath("$.errorDetails[0].reason").value("상세 주소는 필수 항목 입니다."));
	}

	@Test
	@DisplayName("회원 탈퇴 성공 테스트")
	void delete_success() throws Exception {
		// given
		Address address = Address.builder()
			.city("testCity")
			.district("testDistrict")
			.country("testCountry")
			.detail("testDetail")
			.build();
		Member member = Member.builder()
			.username("test@naver.com")
			.nickname("testNickname")
			.memberStatus(MemberStatus.ACTIVE)
			.role(Role.ROLE_USER)
			.address(address)
			.build();
		CustomUserDetails customUserDetails = new CustomUserDetails(member);

		// Authentication 설정
		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

		doNothing().when(memberDeleteService).delete(any(MemberDto.class));
		doNothing().when(cookieService).deleteRefreshTokenFromCookie(any(HttpServletResponse.class));
		doNothing().when(cookieService).deleteRefreshTokenFromCookie(any(HttpServletResponse.class));

		// when
		ResultActions resultActions = mockMvc.perform(delete("/api/v1/members")
			.with(SecurityMockMvcRequestPostProcessors.authentication(authenticationToken))
			.contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions.andExpect(status().isNoContent());
	}

	@WithMockUser
	@DisplayName("비밀번호 변경 성공 테스트")
	@Test
	void password_change_success() throws Exception {
		//given
		Address address = Address.builder()
			.city("testCity")
			.district("testDistrict")
			.country("testCountry")
			.detail("testDetail")
			.build();

		Member givenMember = Member.builder()
			.username("test@naver.com")
			.nickname("testNickname")
			.password("!testPassword1234")
			.memberStatus(MemberStatus.ACTIVE)
			.role(Role.ROLE_USER)
			.address(address)
			.build();

		CustomUserDetails customUserDetails = new CustomUserDetails(givenMember);

		// Authentication 설정
		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

		PasswordChangeForm givenPasswordChangeForm = PasswordChangeForm.builder()
			.originalPassword(givenMember.getPassword())
			.password("!changePassword1234")
			.passwordCheck("!changePassword1234")
			.build();

		doNothing().when(memberService)
			.passwordChange(givenMember.getPassword(), givenPasswordChangeForm.password(), givenMember);

		//when
		ResultActions resultActions = mockMvc.perform(patch("/api/v1/members/password")
			.with(SecurityMockMvcRequestPostProcessors.authentication(authenticationToken))
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenPasswordChangeForm)));

		//then
		resultActions.andExpect(status().isOk());
	}

	@WithMockUser
	@DisplayName("비밀번호 변경시 비밀번호 유효성 검사 실패 테스트")
	@Test
	void password_change_password_valid_success() throws Exception {
		//given
		Address address = Address.builder()
			.city("testCity")
			.district("testDistrict")
			.country("testCountry")
			.detail("testDetail")
			.build();

		Member givenMember = Member.builder()
			.username("test@naver.com")
			.nickname("testNickname")
			.password("!testPassword1234")
			.memberStatus(MemberStatus.ACTIVE)
			.role(Role.ROLE_USER)
			.address(address)
			.build();

		CustomUserDetails customUserDetails = new CustomUserDetails(givenMember);

		// Authentication 설정
		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

		PasswordChangeForm givenPasswordChangeForm = PasswordChangeForm.builder()
			.originalPassword(givenMember.getPassword())
			.password("changePassword1234")
			.passwordCheck("!changePassword1234")
			.build();

		doNothing().when(memberService)
			.passwordChange(givenMember.getPassword(), givenPasswordChangeForm.password(), givenMember);

		//when
		ResultActions resultActions = mockMvc.perform(patch("/api/v1/members/password")
			.with(SecurityMockMvcRequestPostProcessors.authentication(authenticationToken))
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenPasswordChangeForm)));

		//then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("400-1"))
			.andExpect(jsonPath("$.errorDetails[0].field").value("password"))
			.andExpect(jsonPath("$.errorDetails[0].reason")
				.value("공백 없이 비밀번호는 최소 8자리, 최대 20자리이며 대소문자, 숫자, 특수문자 1개씩 필수 입력해야 합니다."));
	}

	@DisplayName("비밀번호 변경시 비밀번호 매치 실패 테스트")
	@Test
	void password_not_match_success() throws Exception {
		//given
		Address address = Address.builder()
			.city("testCity")
			.district("testDistrict")
			.country("testCountry")
			.detail("testDetail")
			.build();

		Member givenMember = Member.builder()
			.username("test@naver.com")
			.nickname("testNickname")
			.password("!testPassword1234")
			.memberStatus(MemberStatus.ACTIVE)
			.role(Role.ROLE_USER)
			.address(address)
			.build();

		CustomUserDetails customUserDetails = new CustomUserDetails(givenMember);

		// Authentication 설정
		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

		PasswordChangeForm givenPasswordChangeForm = PasswordChangeForm.builder()
			.originalPassword(givenMember.getPassword())
			.password("!changePassword12345")
			.passwordCheck("!changePassword1234")
			.build();

		doNothing().when(memberService)
			.passwordChange(givenMember.getPassword(), givenPasswordChangeForm.password(), givenMember);

		//when
		ResultActions resultActions = mockMvc.perform(patch("/api/v1/members/password")
			.with(SecurityMockMvcRequestPostProcessors.authentication(authenticationToken))
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenPasswordChangeForm)));

		//then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("400-1"))
			.andExpect(jsonPath("$.errorDetails[0].field").value("passwordChangeForm"))
			.andExpect(jsonPath("$.errorDetails[0].reason")
				.value("비밀번호와 비밀번호 확인이 일치하지 않습니다."));
	}
}