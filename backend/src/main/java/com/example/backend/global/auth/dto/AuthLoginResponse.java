package com.example.backend.global.auth.dto;

import lombok.Builder;

@Builder
public record AuthLoginResponse (String username) {

    public static AuthLoginResponse of(String username) {
        return new AuthLoginResponse(username);
    }
}
