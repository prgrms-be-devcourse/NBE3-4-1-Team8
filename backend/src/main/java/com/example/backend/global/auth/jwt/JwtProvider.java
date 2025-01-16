package com.example.backend.global.auth.jwt;

import com.example.backend.global.config.JwtConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.example.backend.domain.member.entity.Role;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtConfig jwtConfig;

    public String generateAccessToken(Long id, String username, Role role) {
        return generateToken(username, id, role, jwtConfig.getAccessTokenExpirationTime());
    }

    public String generateRefreshToken(Long id, String username, Role role) {
        return generateToken(username, id, role, jwtConfig.getRefreshTokenExpirationTime());
    }

    private String generateToken(String username, Long id, Role role, Long expirationTime) {
        return Jwts.builder()
            .setSubject(username)
            .claim("id", id)
            .claim("role", role.name())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
            .signWith(jwtConfig.getSecretKey(), SignatureAlgorithm.HS256)
            .compact();
    }
}
