package com.example.backend.global.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * AuthErrorCode
 * <p>Global 예외 발생시 예외 코드를 정의하는 Enum 클래스 입니다.</p>
 *
 * @author vdvhk12
 */
@Getter
public enum AuthErrorCode {
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "해당 유저가 존재하지 않습니다."),
	PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED, "401", "비밀번호가 일치하지 않습니다.");

	final HttpStatus httpStatus;
	final String code;
	final String message;

	AuthErrorCode(HttpStatus httpStatus, String code, String message) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.message = message;
	}
}
