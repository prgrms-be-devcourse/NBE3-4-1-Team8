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
	PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "400-3", "비밀번호가 일치하지 않습니다."),

	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "404-1", "회원이 존재하지 않습니다.");

	final HttpStatus httpStatus;
	final String code;
	final String message;

	MemberErrorCode(HttpStatus httpStatus, String code, String message) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.message = message;
	}
}
