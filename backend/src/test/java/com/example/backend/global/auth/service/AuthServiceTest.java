package com.example.backend.global.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.backend.domain.member.dto.MemberDto;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.entity.Role;
import com.example.backend.domain.member.repository.MemberRepository;
import com.example.backend.global.auth.dto.AuthForm;
import com.example.backend.global.auth.exception.AuthErrorCode;
import com.example.backend.global.auth.exception.AuthException;
import com.example.backend.global.auth.jwt.JwtProvider;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() {
        //given
        MemberDto memberDto = MemberDto.builder()
            .id(1L)
            .username("user@gmail.com")
            .password("password")
            .role(Role.ROLE_USER)
            .build();
        Member member = Member.from(memberDto);

        when(memberRepository.findByUsername(any(String.class))).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);
        when(jwtProvider.generateAccessToken(any(Long.class), any(String.class),
            any(Role.class))).thenReturn("access_token");
        when(jwtProvider.generateRefreshToken(any(Long.class), any(String.class))).thenReturn(
            "refresh_token");

        AuthForm authForm = new AuthForm();
        authForm.setUsername("user@gmail.com");
        authForm.setPassword("password");

        //when
        String result = authService.login(authForm);

        //then
        assertThat(result).isEqualTo("access_token refresh_token");
        verify(memberRepository).findByUsername("user@gmail.com");
        verify(passwordEncoder).matches("password", "password");
        verify(jwtProvider).generateAccessToken(1L, "user@gmail.com", Role.ROLE_USER);
        verify(jwtProvider).generateRefreshToken(1L, "user@gmail.com");

        // saveRefreshToken이 한번 호출 되었는지 검증
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("로그인 실패 - 해당 유저가 없음")
    void loginFail_UserNotFound() {
        // given
        when(memberRepository.findByUsername("user@gmail.com"))
            .thenReturn(Optional.empty());

        AuthForm authForm = new AuthForm();
        authForm.setUsername("user@gmail.com");
        authForm.setPassword("password");

        // when & then
        assertThatThrownBy(() -> authService.login(authForm))
            .isInstanceOf(AuthException.class)
            .hasMessage(AuthErrorCode.USER_NOT_FOUND.getMessage());

        verify(memberRepository).findByUsername("user@gmail.com");
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void loginFail_PasswordNotMatch() {
        // given
        MemberDto memberDto = MemberDto.builder()
            .id(1L)
            .username("user@gmail.com")
            .password("password")
            .role(Role.ROLE_USER)
            .build();
        Member member = Member.from(memberDto);

        when(memberRepository.findByUsername("user@gmail.com")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("pw", "password")).thenReturn(false);

        AuthForm authForm = new AuthForm();
        authForm.setUsername("user@gmail.com");
        authForm.setPassword("pw");

        // when & then
        assertThatThrownBy(() -> authService.login(authForm))
            .isInstanceOf(AuthException.class)
            .hasMessage(AuthErrorCode.PASSWORD_NOT_MATCH.getMessage());

        verify(memberRepository).findByUsername("user@gmail.com");
        verify(passwordEncoder).matches("pw", "password");
    }
}
