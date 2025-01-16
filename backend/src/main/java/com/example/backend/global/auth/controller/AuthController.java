package com.example.backend.global.auth.controller;

import com.example.backend.global.auth.dto.AuthForm;
import com.example.backend.global.auth.dto.AuthLoginResponse;
import com.example.backend.global.auth.dto.AuthResponse;
import com.example.backend.global.auth.service.AuthService;
import com.example.backend.global.auth.service.CookieService;
import com.example.backend.global.response.GenericResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.global.auth.dto.AuthForm;
import com.example.backend.global.auth.dto.AuthResponse;
import com.example.backend.global.auth.dto.EmailCertificationForm;
import com.example.backend.global.auth.service.AuthService;
import com.example.backend.global.response.GenericResponse;
import com.example.backend.global.validation.ValidationSequence;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;
	private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<GenericResponse<AuthLoginResponse>> login(
        @RequestBody @Valid AuthForm authForm, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(authForm);
    public ResponseEntity<GenericResponse<AuthResponse>> login(
        @RequestBody @Validated(ValidationSequence.class) AuthForm authForm, HttpServletResponse response) {
        String[] tokens = authService.login(authForm).split(" ");

        cookieService.addAccessTokenToCookie(authResponse.getAccessToken(), response);
        cookieService.addRefreshTokenToCookie(authResponse.getRefreshToken(), response);

        return ResponseEntity.status(HttpStatus.OK).body(GenericResponse.of(
            AuthLoginResponse.of(authResponse.getId(), authResponse.getUsername()), "로그인 성공"));
    }

    @PostMapping("/logout")
    public ResponseEntity<GenericResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = cookieService.getAccessTokenFromRequest(request);

        authService.logout(accessToken);
        cookieService.deleteRefreshTokenFromCookie(null, response);

        return ResponseEntity.status(HttpStatus.OK).body(GenericResponse.of());
    }

    /**
     * 토큰을 Set-Cookie로 response에 추가하는 메서드
     * @param response
     * @param token
     * @param expirationTime
     */
    private void setTokenCookie(String type, String token, Long expirationTime, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(type, token)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("Strict")
            .maxAge(expirationTime)
            .build();

		// Set-Cookie 헤더로 쿠키를 응답에 추가
		response.addHeader("Set-Cookie", cookie.toString());
	}

	@PostMapping("/verify")
	public ResponseEntity<GenericResponse<Void>> verify(@RequestBody @Validated(ValidationSequence.class)
	EmailCertificationForm emailCertificationForm) {
		authService.verify(emailCertificationForm.username(), emailCertificationForm.certificationCode(),
			emailCertificationForm.verifyType());

		return ResponseEntity.ok().body(GenericResponse.of());
	}
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
