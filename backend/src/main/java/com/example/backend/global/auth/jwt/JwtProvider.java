package com.example.backend.global.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateAccessToken(Long id, String username, Role role) {
        return Jwts.builder()
            .setSubject(username)
            .claim("id", id)
            .claim("role", role.name())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000L))
            .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
            .compact();
    }

    public String generateRefreshToken(Long id, String username) {
        return Jwts.builder()
            .setSubject(username)
            .claim("id", id)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000L))
            .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
            .compact();
    }
}
