package com.example.backend.global.auth.controller;

import com.example.backend.global.auth.dto.AuthForm;
import com.example.backend.global.auth.dto.AuthLoginResponse;
import com.example.backend.global.auth.dto.AuthResponse;
import com.example.backend.global.auth.service.AuthService;
import com.example.backend.global.auth.service.CookieService;
import com.example.backend.global.response.GenericResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.global.auth.dto.EmailCertificationForm;
import com.example.backend.global.validation.ValidationSequence;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;

    @PostMapping("/login")
    public ResponseEntity<GenericResponse<AuthLoginResponse>> login(
        @RequestBody @Validated(ValidationSequence.class) AuthForm authForm, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(authForm);

        cookieService.addAccessTokenToCookie(authResponse.getAccessToken(), response);
        cookieService.addRefreshTokenToCookie(authResponse.getRefreshToken(), response);

        return ResponseEntity.status(HttpStatus.OK).body(GenericResponse.of(
            AuthLoginResponse.of(authResponse.getUsername()), "로그인 성공"));
    }

    @PostMapping("/logout")
    public ResponseEntity<GenericResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = cookieService.getAccessTokenFromRequest(request);

        authService.logout(accessToken);
        cookieService.deleteRefreshTokenFromCookie(response);

        return ResponseEntity.status(HttpStatus.OK).body(GenericResponse.of());
    }

	@PostMapping("/verify")
	public ResponseEntity<GenericResponse<Void>> verify(@RequestBody @Validated(ValidationSequence.class)
	EmailCertificationForm emailCertificationForm) {
		authService.verify(emailCertificationForm.username(), emailCertificationForm.certificationCode(),
			emailCertificationForm.verifyType());

		return ResponseEntity.ok().body(GenericResponse.of());
	}
}
