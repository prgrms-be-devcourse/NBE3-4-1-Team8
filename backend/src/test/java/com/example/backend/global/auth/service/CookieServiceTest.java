package com.example.backend.global.auth.service;

import static org.mockito.BDDMockito.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.backend.global.auth.util.CookieUtils;
import com.example.backend.global.config.JwtConfig;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class CookieServiceTest {

	@Mock
	private CookieUtils cookieUtils;

	@Mock
	private JwtConfig jwtConfig;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@InjectMocks
	private CookieService cookieService;

	@Test
	@DisplayName("Http 요청에서 액세스 토큰 값 추출")
	void getAccessTokenFromRequest() {
		// given
		String expectedToken = "mockAccessToken";
		given(cookieUtils.getTokenFromRequest(request, "accessToken")).willReturn(expectedToken);

		// when
		String actualToken = cookieService.getAccessTokenFromRequest(request);

		// then
		Assertions.assertThat(actualToken).isEqualTo(expectedToken);
		verify(cookieUtils).getTokenFromRequest(request, "accessToken");
	}

	@Test
	@DisplayName("Http 요청에서 리프레시 토큰 값 추출")
	void getRefreshTokenFromRequest() {
		// given
		String expectedToken = "mockRefreshToken";
		given(cookieUtils.getTokenFromRequest(request, "refreshToken")).willReturn(expectedToken);

		// when
		String actualToken = cookieService.getRefreshTokenFromRequest(request);

		// then
		Assertions.assertThat(actualToken).isEqualTo(expectedToken);
		verify(cookieUtils).getTokenFromRequest(request, "refreshToken");
	}

	@Test
	@DisplayName("액세스 토큰 쿠키 저장")
	void addAccessTokenToCookie() {
		// given
		String accessToken = "mockAccessToken";
		long expirationTime = 3600L;
		given(jwtConfig.getAccessTokenExpirationTimeInSeconds()).willReturn(expirationTime);

		// when
		cookieService.addAccessTokenToCookie(accessToken, response);

		// then
		verify(cookieUtils).addTokenToCookie("accessToken", accessToken, expirationTime, response);
	}

	@Test
	@DisplayName("리프레시 토큰 쿠키 저장")
	void addRefreshTokenToCookie() {
		// given
		String refreshToken = "mockRefreshToken";
		long expirationTime = 3600L;
		given(jwtConfig.getRefreshTokenExpirationTimeInSeconds()).willReturn(expirationTime);

		// when
		cookieService.addRefreshTokenToCookie(refreshToken, response);

		// then
		verify(cookieUtils).addTokenToCookie("refreshToken", refreshToken, expirationTime, response);
	}

	@Test
	@DisplayName("액세스 토큰 쿠키 삭제")
	void deleteAccessTokenFromCookie() {
		// When
		cookieService.deleteAccessTokenFromCookie(response);

		// Then
		verify(cookieUtils).addTokenToCookie("accessToken", null, 0L, response);
	}

	@Test
	@DisplayName("리프레시 토큰 쿠키 삭제")
	void deleteRefreshTokenFromCookie() {
		// When
		cookieService.deleteRefreshTokenFromCookie(response);

		// Then
		verify(cookieUtils).addTokenToCookie("refreshToken", null, 0L, response);
	}
}