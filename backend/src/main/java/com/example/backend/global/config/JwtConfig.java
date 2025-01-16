package com.example.backend.global.config;

import io.jsonwebtoken.security.Keys;
import java.security.Key;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    private final Key secretKey;
    private final long accessTokenExpirationTime;
    private final long refreshTokenExpirationTime;

    @Autowired
    public JwtConfig(@Value("${JWT_SECRET}") String secret,
        @Value("${JWT_ACCESS_TOKEN_EXPIRATION_TIME}") long accessTokenExpirationTime,
        @Value("${JWT_REFRESH_TOKEN_EXPIRATION_TIME}") long refreshTokenExpirationTime) {
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Key getSecretKey() {
        return this.secretKey;
    }

    public long getAccessTokenExpirationTime() {
        return this.accessTokenExpirationTime;
    }

    public long getRefreshTokenExpirationTime() {
        return this.refreshTokenExpirationTime;
    }

    // 쿠키의 유효기간을 초 단위로 변환
    public long getAccessTokenExpirationTimeInSeconds() {
        return (accessTokenExpirationTime / 1000) * 2;
    }

    public long getRefreshTokenExpirationTimeInSeconds() {
        return refreshTokenExpirationTime / 1000;
    }
}
