package com.example.backend.global.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    private String username;

    public static AuthResponse of(String username) {
        return AuthResponse.builder()
            .username(username)
            .build();
    }
}
