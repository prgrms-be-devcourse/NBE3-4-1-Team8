package com.example.backend.global.auth.service;

import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.repository.MemberRepository;
import com.example.backend.global.auth.dto.AuthForm;
import com.example.backend.global.auth.exception.AuthErrorCode;
import com.example.backend.global.auth.exception.AuthException;
import com.example.backend.global.auth.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final MemberRepository memberRepository;

    public String login(AuthForm authForm) {
        Member member = memberRepository.findByUsername(authForm.getUsername())
            .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        if (!member.checkPassword(authForm.getPassword(), passwordEncoder)) {
            throw new AuthException(AuthErrorCode.PASSWORD_NOT_MATCH);
        }

        String accessToken = jwtProvider.generateAccessToken(member.getId(), member.getUsername(), member.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(member.getId(), member.getUsername());
        refreshTokenService.saveRefreshToken(member.getUsername(), refreshToken);

        return accessToken + " " + refreshToken;
    }

    public void logout(String accessToken) {
        String username = jwtProvider.getUsernameFromToken(accessToken);
        refreshTokenService.deleteRefreshToken(username);

        // 시큐리티 컨텍스트 초기화
        SecurityContextHolder.clearContext();
    }
}
