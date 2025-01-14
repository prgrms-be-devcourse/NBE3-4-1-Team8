package com.example.backend.global.response;

import java.time.ZonedDateTime;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

/**
 * GenericResponse
 * <p>요청이 성공했을 때 공통 응답 클래스 입니다.</p>
 *
 * @author Kim Dong O
 */
@Getter
public class GenericResponse<T> {
	private final ZonedDateTime timestamp;
	private boolean isSuccess;
	private String message;
	private final T data;

	@Builder(access = AccessLevel.PRIVATE)
	private GenericResponse(T data, String message, boolean isSuccess) {
		this.timestamp = ZonedDateTime.now();
		this.data = data;
		this.message = message;
		this.isSuccess = isSuccess;
	}
}