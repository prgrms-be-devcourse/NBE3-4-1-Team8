package com.example.backend.global.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Component;

import com.example.backend.domain.member.entity.Role;

@Component
public class JwtProvider {

    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 30 * 60 * 1000; // 30분
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000;  // 24시간

    public String generateAccessToken(Long id, String username, Role role) {
        return Jwts.builder()
            .setSubject(username)
            .claim("id", id)
            .claim("role", role.name())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
            .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
            .compact();
    }

    public String generateRefreshToken(Long id, String username) {
        return Jwts.builder()
            .setSubject(username)
            .claim("id", id)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
            .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
            .compact();
    }
}
