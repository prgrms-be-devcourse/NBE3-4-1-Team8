package com.example.backend.global.auth.util;

import com.example.backend.global.auth.exception.AuthErrorCode;
import com.example.backend.global.response.HttpErrorInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FilterUtils {

    private final ObjectMapper objectMapper;

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
        response.setStatus(Integer.parseInt(httpErrorInfo.code()));
    }
}
