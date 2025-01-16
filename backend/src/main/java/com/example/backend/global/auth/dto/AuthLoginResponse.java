package com.example.backend.global.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthLoginResponse {

    private Long id;
    private String username;

    public static AuthLoginResponse of(Long id, String username) {
        return AuthLoginResponse.builder()
            .id(id)
            .username(username)
            .build();
    }
}
