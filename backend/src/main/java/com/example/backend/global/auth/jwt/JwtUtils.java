package com.example.backend.global.auth.jwt;

import com.example.backend.global.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtConfig jwtConfig;
    private final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(jwtConfig.getSecretKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public Long getUserIdFromToken(String token) {
        return parseClaims(token).get("id", Long.class);
    }

    public String getRoleFromToken(String token) {
        return parseClaims(token).get("role", String.class);
    }

    public String validateToken(String token) {
        try {
            parseClaims(token);
            return "valid";
        } catch (ExpiredJwtException e) {
            logger.error("토큰의 유효기간이 만료되었습니다.", e);
            return "expired";
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("유효하지 않은 토큰입니다.", e);
            return "invalid";
        }
    }
}
