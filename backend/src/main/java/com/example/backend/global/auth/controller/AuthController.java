package com.example.backend.global.auth.controller;

import static com.example.backend.global.auth.jwt.JwtProvider.REFRESH_TOKEN_EXPIRATION_TIME;

import com.example.backend.global.auth.dto.AuthForm;
import com.example.backend.global.auth.dto.AuthResponse;
import com.example.backend.global.auth.service.AuthService;
import com.example.backend.global.response.GenericResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<GenericResponse<AuthResponse>> login(@RequestBody @Valid AuthForm authForm, HttpServletResponse response) {
        String[] tokens = authService.login(authForm).split(" ");

        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokens[1])
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("Strict")
            .maxAge(REFRESH_TOKEN_EXPIRATION_TIME)
            .build();

        // Set-Cookie 헤더로 쿠키를 응답에 추가
        response.addHeader("Set-Cookie", cookie.toString());
        response.addHeader("Authorization", "Bearer " + tokens[0]);
        return ResponseEntity.status(HttpStatus.OK)
            .body(GenericResponse.of(AuthResponse.of(authForm.getUsername()), "로그인 성공"));
    }
}