package com.example.backend.global.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {

    public String getTokenFromRequest(HttpServletRequest request, String type) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (type.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void addTokenToCookie(String type, String token, Long expirationTime, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(type, token)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("Strict")
            .maxAge(expirationTime)
            .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
