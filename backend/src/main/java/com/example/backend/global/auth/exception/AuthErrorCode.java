package com.example.backend.global.auth.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * AuthErrorCode
 * <p>Auth 예외 발생시 예외 코드를 정의하는 Enum 클래스 입니다.</p>
 *
 * @author vdvhk12
 */
@Getter
public enum AuthErrorCode {
	UNKNOWN_SERVER(HttpStatus.INTERNAL_SERVER_ERROR, "500-1", "요청 처리중 서버에서 예외가 발생했습니다."),
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "404-1", "해당 유저가 존재하지 않습니다."),
	CERTIFICATION_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "404-2", "해당 이메일의 인증 코드 정보가 존재하지 않습니다."),

	PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED, "401-1", "비밀번호가 일치하지 않습니다."),
	TOKEN_NOT_VALID(HttpStatus.UNAUTHORIZED, "401-2", "유효하지 않은 토큰입니다."),
	TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "401-3", "토큰이 없습니다."),
	VERIFY_TYPE_NOT_MATCH(HttpStatus.UNAUTHORIZED, "401-4", "인증 타입이 일치하지 않습니다."),
	CERTIFICATION_CODE_NOT_MATCH(HttpStatus.UNAUTHORIZED, "401-5", "인증 코드가 일치하지 않습니다."),
	NOT_CERTIFICATION(HttpStatus.UNAUTHORIZED, "401-6", "이메일 인증을 하지 않았습니다."),
	REFRESH_TOKEN_NOT_VALID(HttpStatus.UNAUTHORIZED, "401-7", "유효하지 않은 리프레시 토큰입니다."),
	REFRESH_TOKEN_NOT_MATCH(HttpStatus.UNAUTHORIZED, "401-8", "저장된 리프레시 토큰과 일치하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "401-9", "리프레시 토큰을 찾을 수 없습니다."),

	ALREADY_CERTIFIED(HttpStatus.BAD_REQUEST, "400-1", "이미 이메일 인증을 하셨습니다."),
	TOO_MANY_RESEND_ATTEMPTS(HttpStatus.BAD_REQUEST, "400-2", "5회 이상 시도하셨습니다. 잠시후 다시 시도해주세요.");

	final HttpStatus httpStatus;
	final String code;
	final String message;

	AuthErrorCode(HttpStatus httpStatus, String code, String message) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.message = message;
	}
}
