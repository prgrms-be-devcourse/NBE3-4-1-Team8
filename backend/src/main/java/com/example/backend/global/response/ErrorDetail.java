package com.example.backend.global.response;

public record ErrorDetail(String field, String reason, String message) {
	public static ErrorDetail of(String field, String reason, String message) {
		return new ErrorDetail(field, reason, message);
	}
}
