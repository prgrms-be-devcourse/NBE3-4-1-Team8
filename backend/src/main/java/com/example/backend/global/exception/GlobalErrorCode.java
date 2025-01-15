package com.example.backend.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * GlobalErrorCode
 * <p>Global 예외 발생시 예외 코드를 정의하는 Enum 클래스 입니다.</p>
 *
 * @author Kim Dong O
 */
@Getter
public enum GlobalErrorCode {
	NOT_VALID(HttpStatus.BAD_REQUEST, "400-1", "요청하신 유효성 검증에 실패하였습니다.");

	final HttpStatus httpStatus;
	final String code;
	final String message;

	GlobalErrorCode(HttpStatus httpStatus, String code, String message) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.message = message;
	}
}
