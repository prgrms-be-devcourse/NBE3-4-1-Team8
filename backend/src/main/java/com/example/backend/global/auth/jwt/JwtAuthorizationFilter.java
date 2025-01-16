package com.example.backend.global.auth.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.backend.global.auth.exception.AuthErrorCode;
import com.example.backend.global.auth.model.CustomUserDetails;
import com.example.backend.global.auth.service.CustomUserDetailsService;
import com.example.backend.global.auth.service.RefreshTokenService;
import com.example.backend.global.response.HttpErrorInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenService refreshTokenService;

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
        }

        try {
            // 토큰 유효성 검사
            if (!jwtProvider.validateToken(token)) {
                createErrorInfo(AuthErrorCode.TOKEN_NOT_VALID, request, response);
                return;
            }

            // 토큰에서 사용자 정보를 추출하고 인증 처리
            CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(
                jwtProvider.getUsernameFromToken(token));

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException e) {
            // 여기서 리프레시 토큰 검증 후 유효하면 엑세스 토큰 재발급


            filterChain.doFilter(request, response);
        } catch (UsernameNotFoundException e) {
            createErrorInfo(AuthErrorCode.USER_NOT_FOUND, request, response);
            return;
        }
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    customUserDetails, null, customUserDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (UsernameNotFoundException e) {
                createErrorInfo(AuthErrorCode.MEMBER_NOT_FOUND, request, response);
                return;
            }

        }
        filterChain.doFilter(request, response);
    }

    // 예외 발생시 에러 메세지 생성 후 응답객체에 추가하는 메서드
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

    // 쿠키에서 accessToken을 꺼내오는 메서드
    private String getTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // 권한 체크가 필요 없는 URL인지 확인하는 메서드
    private boolean isUnprotectedUrl(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/api/v1/auth/login") || path.equals("/api/v1/members/join")
            || path.equals("/api/v1/auth/refresh") || path.equals("/api/v1/auth/verify");
    }
}
