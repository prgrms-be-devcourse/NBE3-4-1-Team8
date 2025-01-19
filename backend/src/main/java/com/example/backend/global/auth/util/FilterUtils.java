package com.example.backend.global.auth.util;

import com.example.backend.global.auth.exception.AuthErrorCode;
import com.example.backend.global.response.HttpErrorInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FilterUtils {

    private final ObjectMapper objectMapper;

    // 보호되지 않는 특정 URL 목록
    private static final List<String> UNPROTECTED_URLS = List.of(
        "/api/v1/members/join",
        "/api/v1/auth/login",
        "/api/v1/auth/code",
        "/api/v1/auth/verify"
    );

    // 보호된 URL 패턴 목록
    private static final List<String> PROTECTED_URLS = List.of(
        "/api/v1/members",
        "/api/v1/auth",
        "/api/v1/products",
        "/api/v1/orders",
        "/api/v1/carts"
    );

    //필터에서 예외 발생 시 에러 메세지 생성 후 응답객체에 추가하는 메서드
    public void createErrorInfo(AuthErrorCode authErrorCode, HttpServletRequest request,
        HttpServletResponse response) throws IOException {
        HttpErrorInfo httpErrorInfo = HttpErrorInfo.of(
            authErrorCode.getCode(),
            request.getRequestURI(),
            authErrorCode.getMessage()
        );
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(httpErrorInfo));
        response.setStatus(authErrorCode.getHttpStatus().value());
    }

    // 권한 체크가 필요 없는 URL인지 확인하는 메서드
    public boolean isUnprotectedUrl(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // GET 메서드인 경우
        if ("GET".equalsIgnoreCase(method)) {
            // GET 요청은 보호되지 않는 URL 목록에 있는지 확인
            if (path.startsWith("/api/v1/products")) {
                return true; // GET 요청은 보호되지 않는 URL로 간주
            }
        }

        // 보호되지 않는 URL 패턴 예외 처리
        if (UNPROTECTED_URLS.contains(path)) {
            return true; // 보호되지 않는 URL로 간주
        }

        // 권한이 필요한 URL 패턴에 매칭되는지 확인
        for (String protectedUrl : PROTECTED_URLS) {
            if (path.startsWith(protectedUrl)) {
                return false; // 권한이 필요한 URL이면 false 반환
            }
        }

        return true; // 나머지 URL은 모두 허용
    }
}
