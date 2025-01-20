package com.example.backend.global.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.global.auth.dto.AuthForm;
import com.example.backend.global.auth.dto.AuthLoginResponse;
import com.example.backend.global.auth.dto.AuthResponse;
import com.example.backend.global.auth.dto.EmailCertificationForm;
import com.example.backend.global.auth.dto.SendEmailCertificationCodeForm;
import com.example.backend.global.auth.service.AuthService;
import com.example.backend.global.auth.service.CookieService;
import com.example.backend.global.response.GenericResponse;
import com.example.backend.global.validation.ValidationSequence;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "ApiV1AuthController", description = "API 인증 컨트롤러")
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;

	@Operation(summary = "로그인", description = "accessToken, refreshToken을 발급, 쿠키로 전달")
    @PostMapping("/login")
    public ResponseEntity<GenericResponse<AuthLoginResponse>> login(
        @RequestBody @Validated(ValidationSequence.class) AuthForm authForm, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(authForm);

        cookieService.addAccessTokenToCookie(authResponse.accessToken(), response);
        cookieService.addRefreshTokenToCookie(authResponse.refreshToken(), response);

        return ResponseEntity.status(HttpStatus.OK).body(GenericResponse.of(
			AuthLoginResponse.of(authResponse.username()), "로그인 성공"));
    }

	@Operation(summary = "로그아웃", description = "accessToken, refreshToken 을 제거")
    @PostMapping("/logout")
    public ResponseEntity<GenericResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = cookieService.getAccessTokenFromRequest(request);

        authService.logout(accessToken);
		cookieService.deleteAccessTokenFromCookie(response);
        cookieService.deleteRefreshTokenFromCookie(response);

        return ResponseEntity.status(HttpStatus.OK).body(GenericResponse.of("로그아웃 성공"));
    }

	@Operation(summary = "이메일 인증")
	@PostMapping("/verify")
	public ResponseEntity<GenericResponse<Void>> verify(@RequestBody @Validated(ValidationSequence.class)
	EmailCertificationForm emailCertificationForm) {
		authService.verify(emailCertificationForm.username(), emailCertificationForm.certificationCode(),
			emailCertificationForm.verifyType());

		return ResponseEntity.ok().body(GenericResponse.of());
	}

	@Operation(summary = "이메일 인증 코드 발송")
	@PostMapping("/code")
	public ResponseEntity<GenericResponse<Void>> code(@RequestBody @Validated(ValidationSequence.class)
		SendEmailCertificationCodeForm sendEmailCertificationCodeForm) {
		authService.send(sendEmailCertificationCodeForm.username(), sendEmailCertificationCodeForm.verifyType());

		return ResponseEntity.ok().body(GenericResponse.of());
	}
}
