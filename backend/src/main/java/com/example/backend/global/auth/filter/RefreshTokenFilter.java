package com.example.backend.global.auth.filter;

import com.example.backend.domain.member.entity.Role;
import com.example.backend.global.auth.exception.AuthErrorCode;
import com.example.backend.global.auth.jwt.JwtProvider;
import com.example.backend.global.auth.jwt.JwtUtils;
import com.example.backend.global.auth.model.CustomUserDetails;
import com.example.backend.global.auth.service.CookieService;
import com.example.backend.global.auth.service.CustomUserDetailsService;
import com.example.backend.global.auth.service.RefreshTokenService;
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
public class RefreshTokenFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final JwtUtils jwtUtils;
    private final FilterUtils filterUtils;
    private final CookieService cookieService;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        if(filterUtils.isUnprotectedUrl(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 1: SecurityContextHolder 확인
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            // 이미 인증된 사용자인 경우 다음 필터로 넘김
            filterChain.doFilter(request, response);
            return;
        }

        // step2: 리프레시 토큰의 사용자 정보 추출 후, 저장된 리프레시 토큰과 일치하는지 검증하고 에러핸들링
        String refreshToken = cookieService.getRefreshTokenFromRequest(request);
        String username = jwtUtils.getUsernameFromToken(refreshToken);
        if(!refreshTokenService.isValidRefreshToken(username, refreshToken)) {
            logout(response);
            filterUtils.createErrorInfo(AuthErrorCode.REFRESH_TOKEN_NOT_MATCH, request, response);
            return;
        }

        // step3: 새로운 액세스 토큰, 리프레시 토큰 생성
        Long id = jwtUtils.getUserIdFromToken(refreshToken);
        Role role = Role.valueOf(jwtUtils.getRoleFromToken(refreshToken));
        String newAccessToken = jwtProvider.generateAccessToken(id, username, role);
        String newRefreshToken = jwtProvider.generateRefreshToken(id, username, role);

        // step4: SecurityContext 에 사용자 정보 추가
        try {
            CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (UsernameNotFoundException e) {
            filterUtils.createErrorInfo(AuthErrorCode.MEMBER_NOT_FOUND, request, response);
        }

        // step5: 액세스 토큰을 쿠키에 추가
        cookieService.addAccessTokenToCookie(newAccessToken, response);
        cookieService.addRefreshTokenToCookie(newRefreshToken, response);

        // step6: 레디스에 리프레시 토큰 저장
        refreshTokenService.saveRefreshToken(username, newRefreshToken);

        // step7: 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    private void logout(HttpServletResponse response) {
        cookieService.deleteAccessTokenFromCookie(response);
        cookieService.deleteRefreshTokenFromCookie(response);
        SecurityContextHolder.clearContext();
    }
}
