package com.example.backend.domain.member.exception;

import org.springframework.http.HttpStatus;


public class MemberException extends RuntimeException {

	private MemberErrorCode memberErrorCode;

	public MemberException(MemberErrorCode memberErrorCode) {
		super(memberErrorCode.message);
		this.memberErrorCode = memberErrorCode;
	}

	public HttpStatus getStatus() {
		return memberErrorCode.httpStatus;
	}

	public String getCode() {
		return memberErrorCode.code;
	}
}
