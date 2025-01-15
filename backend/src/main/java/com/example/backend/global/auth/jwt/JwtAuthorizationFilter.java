package com.example.backend.global.auth.jwt;

import static com.example.backend.global.auth.jwt.JwtProvider.SECRET_KEY;

import com.example.backend.global.auth.exception.AuthErrorCode;
import com.example.backend.global.auth.model.CustomUserDetails;
import com.example.backend.global.auth.service.CustomUserDetailsService;
import com.example.backend.global.response.HttpErrorInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        if(isUnprotectedUrl(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getTokenFromRequest(request);

        if (token == null) {
            createErrorInfo(AuthErrorCode.TOKEN_MISSING, request, response);
            return;
        } else if (!validateToken(token)) {
            createErrorInfo(AuthErrorCode.TOKEN_NOT_VALID, request, response);
            return;
        } else {
            CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(
                getUsernameFromToken(token));

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private void createErrorInfo(AuthErrorCode tokenMissing, HttpServletRequest request,
        HttpServletResponse response) throws IOException {
        HttpErrorInfo httpErrorInfo = HttpErrorInfo.of(
            tokenMissing.getCode(),
            request.getRequestURI(),
            tokenMissing.getMessage()
        );
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(httpErrorInfo));
        response.setStatus(Integer.parseInt(httpErrorInfo.code()));
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 권한 체크가 필요 없는 URL인지 확인하는 메서드
    private boolean isUnprotectedUrl(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/api/v1/auth/login") || path.equals("/api/v1/members/join")
            || path.equals("/api/v1/auth/refresh");
    }

    private boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Invalid JWT token", e);
        }
        return false;
    }

    private String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(SECRET_KEY)
            .build()
            .parseClaimsJws(token)
            .getBody();
        return claims.getSubject();
    }
}
