package com.example.backend.global.auth.filter;

import com.example.backend.global.auth.exception.AuthErrorCode;
import com.example.backend.global.auth.jwt.JwtUtils;
import com.example.backend.global.auth.model.CustomUserDetails;
import com.example.backend.global.auth.service.CookieService;
import com.example.backend.global.auth.service.CustomUserDetailsService;
import com.example.backend.global.auth.util.FilterUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final FilterUtils filterUtils;
    private final CookieService cookieService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        if(filterUtils.isUnprotectedUrl(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = cookieService.getAccessTokenFromRequest(request);
        if (accessToken == null) {
            String refreshToken = cookieService.getRefreshTokenFromRequest(request);
            if(refreshToken == null) {
                filterUtils.createErrorInfo(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND, request, response);
                return;
            }
            // TODO: 액세스 토큰이 쿠키에 없을 때, 리프레시 토큰 필터 처리
            System.out.println("JwtAuthorizationFilter.doFilterInternal => 액세스 토큰 값이 null 이고, 리프레시 토큰이 null이 아닐때 다음 필터 진행");
            filterChain.doFilter(request, response);
            return;
        }

        String validationResult = jwtUtils.validateToken(accessToken);
        if(validationResult.equals("invalid")) {
            filterUtils.createErrorInfo(AuthErrorCode.TOKEN_NOT_VALID, request, response);
            return;
        } else if(validationResult.equals("expired")) {
            // TODO: 쿠키에 값이 있는데, 액세스 토큰이 만료 되었을 때, 리프레시 토큰 필터 처리
            System.out.println("JwtAuthorizationFilter.doFilterInternal => 액세스 토큰이 만료일 때, 다음 필터로 넘김");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(
                jwtUtils.getUsernameFromToken(accessToken));

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (UsernameNotFoundException e) {
            filterUtils.createErrorInfo(AuthErrorCode.USER_NOT_FOUND, request, response);
        }

        filterChain.doFilter(request, response);
    }
}
