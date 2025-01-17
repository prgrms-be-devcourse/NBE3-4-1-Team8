package com.example.backend.domain.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.domain.member.dto.MemberSignupForm;
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
		@RequestBody @Validated(ValidationSequence.class) MemberSignupForm memberSignupForm) {

		memberService.signup(memberSignupForm.username(), memberSignupForm.nickname(),
			memberSignupForm.password(), memberSignupForm.city(),
			memberSignupForm.district(), memberSignupForm.country(), memberSignupForm.detail());

		return ResponseEntity.status(HttpStatus.CREATED).body(GenericResponse.of());
	}
}
