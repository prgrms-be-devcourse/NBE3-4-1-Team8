package com.example.backend.global.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthLoginResponse {

    private String username;

    public static AuthLoginResponse of(String username) {
        return AuthLoginResponse.builder()
            .username(username)
            .build();
    }
}
