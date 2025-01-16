package com.example.backend.global.auth.service;

import com.example.backend.global.auth.util.CookieUtils;
import com.example.backend.global.config.JwtConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookieService {

    private final CookieUtils cookieUtils;
    private final JwtConfig jwtConfig;

    public String getAccessTokenFromRequest(HttpServletRequest request) {
        return cookieUtils.getTokenFromRequest(request, "accessToken");
    }

    public void addAccessTokenToCookie(String accessToken, HttpServletResponse response) {
        cookieUtils.addTokenToCookie("accessToken", accessToken, jwtConfig.getAccessTokenExpirationTimeInSeconds(), response);
    }

    public void addRefreshTokenToCookie(String refreshToken, HttpServletResponse response) {
        cookieUtils.addTokenToCookie("refreshToken", refreshToken, jwtConfig.getRefreshTokenExpirationTimeInSeconds(), response);
    }

    public void deleteRefreshTokenFromCookie(String refreshToken, HttpServletResponse response) {
        cookieUtils.addTokenToCookie("refreshToken", refreshToken, 0L, response);
    }
}
