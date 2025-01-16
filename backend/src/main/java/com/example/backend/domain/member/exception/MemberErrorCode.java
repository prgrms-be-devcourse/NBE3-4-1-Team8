package com.example.backend.domain.member.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * GlobalErrorCode
 * <p>Global 예외 발생시 예외 코드를 정의하는 Enum 클래스 입니다.</p>
 *
 * @author Kim Dong O
 */
@Getter
public enum MemberErrorCode {
	EXISTS_USERNAME(HttpStatus.BAD_REQUEST, "400-1", "중복된 이메일 입니다."),
	EXISTS_NICKNAME(HttpStatus.BAD_REQUEST, "400-2", "중복된 닉네임 입니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "403-1", "접근 권한이 없습니다."),
	NOT_FOUND(HttpStatus.NOT_FOUND,"404-1", "해당 리소스를 찾을 수 없습니다"),
	SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500-1", "서버 에러"),
	NULL_POINT(HttpStatus.INTERNAL_SERVER_ERROR, "500-2", "Null Point");


	final HttpStatus httpStatus;
	final String code;
	final String message;

	MemberErrorCode(HttpStatus httpStatus, String code, String message) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.message = message;
	}
}
