package com.example.backend.global.response;

import java.time.ZonedDateTime;

import lombok.Getter;

@Getter
public class GenericResponse<T> {
	private final ZonedDateTime timestamp;
	private boolean isSuccess;
	private String message;
	private final T data;

	private GenericResponse(T data, String message, boolean isSuccess) {
		this.timestamp = ZonedDateTime.now();
		this.data = data;
		this.message = message;
		this.isSuccess = isSuccess;
	}
}