package com.example.backend.global.response;

import java.time.ZonedDateTime;
import java.util.List;

public record HttpErrorInfo(String code, String path, String message, List<ErrorDetail> errorDetails, ZonedDateTime timeStamp) {
	// of 메서드를 통한 팩토리 메서드 구현
	public static HttpErrorInfo of(String code, String path, String message, List<ErrorDetail> errorDetails) {
		return new HttpErrorInfo(code, path, message, errorDetails, ZonedDateTime.now());
	}

}
