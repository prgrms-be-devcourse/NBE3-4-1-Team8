package com.example.backend.domain.orders.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OrdersErrorCode {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "400-1", "잘못된 요청입니다."),
    REFERENCE_INTEGRITY_ERROR(HttpStatus.BAD_REQUEST, "400-2", "참조 무결성 에러 유효하지 않은 데이터"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "403-1", "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND,"404-1", "해당 리소스를 찾을 수 없습니다"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500-1", "서버 에러"),
    NULL_POINT(HttpStatus.INTERNAL_SERVER_ERROR, "500-2", "Null Point");

    final HttpStatus httpStatus;
    final String code;
    final String message;

    OrdersErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
