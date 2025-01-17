package com.example.backend.global.auth.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * AuthErrorCode
 * <p>Global 예외 발생시 예외 코드를 정의하는 Enum 클래스 입니다.</p>
 *
 * @author vdvhk12
 */
@Getter
public enum AuthErrorCode {
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "해당 유저가 존재하지 않습니다."),
	PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED, "401", "비밀번호가 일치하지 않습니다."),
	TOKEN_NOT_VALID(HttpStatus.UNAUTHORIZED, "401", "유효하지 않은 토큰입니다."),
	TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "401", "토큰이 없습니다."),
	CERTIFICATION_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "404-1", "해당 이메일의 인증 코드 정보가 존재하지 않습니다."),
	VERIFY_TYPE_NOT_MATCH(HttpStatus.UNAUTHORIZED, "401-1", "인증 타입이 일치하지 않습니다."),
	CERTIFICATION_CODE_NOT_MATCH(HttpStatus.UNAUTHORIZED, "401-2", "인증 코드가 일치하지 않습니다.");

	final HttpStatus httpStatus;
	final String code;
	final String message;

	AuthErrorCode(HttpStatus httpStatus, String code, String message) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.message = message;
	}
}
