package com.example.backend.global.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    private Long id;
    private String username;
    private String accessToken;
    private String refreshToken;

    public static AuthResponse of(Long id, String username, String accessToken, String refreshToken) {
        return AuthResponse.builder()
            .id(id)
            .username(username)
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }
}
