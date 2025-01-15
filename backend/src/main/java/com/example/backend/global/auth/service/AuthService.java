package com.example.backend.global.auth.service;

import com.example.backend.global.auth.dto.AuthForm;
import com.example.backend.global.auth.exception.AuthErrorCode;
import com.example.backend.global.auth.exception.AuthException;
import com.example.backend.global.auth.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public String login(AuthForm authForm) {
        Member member = memberRepository.findByUsername(authForm.getUsername())
            .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(authForm.getPassword(), member.getPassword())) {
            throw new AuthException(AuthErrorCode.PASSWORD_NOT_MATCH);
        }

        String accessToken = jwtProvider.generateAccessToken(member.getId(), member.getUsername(), member.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(member.getId(), member.getUsername());
        saveRefreshToken(member, refreshToken);

        return accessToken + " " + refreshToken;
    }

    private void saveRefreshToken(Member member, String refreshToken) {
        memberRepository.save(member.toBuilder()
            .refreshToken(refreshToken)
            .build());
    }
}
