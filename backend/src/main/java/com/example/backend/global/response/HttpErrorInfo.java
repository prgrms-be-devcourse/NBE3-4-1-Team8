package com.example.backend.global.response;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;

/**
 * HttpErrorInfo
 * <p>예외 발생시 사용할 공통 응답 클래스</p>
 * @param code
 * @param path
 * @param message
 * @param errorDetails
 * @param timeStamp
 * @author Kim Dong O
 */
public record HttpErrorInfo(String code, String path, String message, ZonedDateTime timeStamp, List<ErrorDetail> errorDetails) {

	@Builder(access = AccessLevel.PROTECTED)
	public HttpErrorInfo(String code, String path, String message, ZonedDateTime timeStamp,
		List<ErrorDetail> errorDetails) {
		this.code = code;
		this.path = path;
		this.message = message;
		this.timeStamp = timeStamp;
		this.errorDetails = errorDetails;
	}

	/**
	 * HttpErrorInfo 생성 팩토리 메서드
	 *
	 * @param code 커스텀 예외 코드
	 * @param path 예외가 발생한 요청 경로
	 * @param message 예외가 발생한 이유
	 * @param errorDetails 필드 에러 정보
	 * @author Kim Dong O
	 * @return {@link HttpErrorInfo}
	 */
	public static HttpErrorInfo of(String code, String path, String message, List<ErrorDetail> errorDetails) {
		return HttpErrorInfo.builder()
			.code(code)
			.path(path)
			.message(message)
			.errorDetails(errorDetails)
			.timeStamp(ZonedDateTime.now())
			.build();
	}

}
