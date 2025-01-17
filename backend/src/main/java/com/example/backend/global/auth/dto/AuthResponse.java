package com.example.backend.global.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    private String username;
    private String accessToken;
    private String refreshToken;

    public static AuthResponse of(String username, String accessToken, String refreshToken) {
        return AuthResponse.builder()
            .username(username)
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }
}
