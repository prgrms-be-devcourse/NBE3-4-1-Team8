package com.example.backend.global.auth.exception;

import org.springframework.http.HttpStatus;

public class AuthException extends RuntimeException {
	private AuthErrorCode authErrorCode;

	public AuthException(AuthErrorCode authErrorCode) {
		super(authErrorCode.message);
		this.authErrorCode = authErrorCode;
	}

	public HttpStatus getStatus() {
		return authErrorCode.httpStatus;
	}

	public String getCode() {
		return authErrorCode.code;
	}
}
