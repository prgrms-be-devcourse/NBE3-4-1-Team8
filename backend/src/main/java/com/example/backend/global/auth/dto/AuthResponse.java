package com.example.backend.global.auth.dto;

import lombok.Builder;

@Builder
public record AuthResponse (String username, String accessToken, String refreshToken) {

    public static AuthResponse of(String username, String accessToken, String refreshToken) {
        return new AuthResponse(username, accessToken, refreshToken);
    }
}
