package com.example.backend.domain.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.domain.member.conveter.MemberConverter;
import com.example.backend.domain.member.dto.MemberInfoResponse;
import com.example.backend.domain.member.dto.MemberModifyForm;
import com.example.backend.domain.member.dto.MemberSignupForm;
import com.example.backend.domain.member.service.MemberDeleteService;
import com.example.backend.domain.member.service.MemberService;
import com.example.backend.global.auth.model.CustomUserDetails;
import com.example.backend.global.auth.service.CookieService;
import com.example.backend.global.response.GenericResponse;
import com.example.backend.global.validation.ValidationSequence;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@Tag(name = "ApiV1MemberController", description = "API 회원 컨트롤러")
public class MemberController {
	private final MemberService memberService;
	private final MemberDeleteService memberDeleteService;
	private final CookieService cookieService;

	@Operation(summary = "회원 가입")
	@PostMapping("/join")
	public ResponseEntity<GenericResponse<Void>> signUp(
		@RequestBody @Validated(ValidationSequence.class) MemberSignupForm memberSignupForm) {

		memberService.signup(memberSignupForm.username(), memberSignupForm.nickname(),
			memberSignupForm.password(), memberSignupForm.city(),
			memberSignupForm.district(), memberSignupForm.country(), memberSignupForm.detail());

		return ResponseEntity.status(HttpStatus.CREATED).body(GenericResponse.of());
	}

	@Operation(summary = "회원 정보 조회")
	@GetMapping
	public ResponseEntity<GenericResponse<MemberInfoResponse>> getMemberInfo(
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GenericResponse.of(MemberConverter.from(customUserDetails.getMember())));
	}

	@Operation(summary = "회원 정보 수정")
	@PatchMapping
	public ResponseEntity<GenericResponse<MemberInfoResponse>> modify(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@RequestBody @Validated(ValidationSequence.class) MemberModifyForm memberModifyForm) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GenericResponse.of(memberService.modify(customUserDetails.getMember().toModel(), memberModifyForm)));
	}

	@Operation(summary = "회원 탈퇴")
	@DeleteMapping
	public ResponseEntity<GenericResponse<Void>> delete(@AuthenticationPrincipal CustomUserDetails customUserDetails,
		HttpServletResponse response) {
		cookieService.deleteAccessTokenFromCookie(response);
		cookieService.deleteRefreshTokenFromCookie(response);
		memberDeleteService.delete(customUserDetails.getMember().toModel());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(GenericResponse.of());
	}
}
