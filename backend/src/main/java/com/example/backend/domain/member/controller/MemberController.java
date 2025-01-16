package com.example.backend.domain.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.domain.member.dto.MemberSignupRequest;
import com.example.backend.domain.member.service.MemberService;
import com.example.backend.global.response.GenericResponse;
import com.example.backend.global.validation.ValidationSequence;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {
	private final MemberService memberService;

	@PostMapping("/join")
	public ResponseEntity<GenericResponse<Void>> signUp(
		@RequestBody @Validated(ValidationSequence.class) MemberSignupRequest memberSignupRequest) {

		memberService.signup(memberSignupRequest.username(), memberSignupRequest.nickname(),
			memberSignupRequest.password(), memberSignupRequest.city(),
			memberSignupRequest.district(), memberSignupRequest.country(), memberSignupRequest.detail());

		return ResponseEntity.status(HttpStatus.CREATED).body(GenericResponse.of());
	}
}
