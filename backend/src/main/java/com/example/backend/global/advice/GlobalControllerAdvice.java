package com.example.backend.global.advice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.backend.global.exception.GlobalErrorCode;
import com.example.backend.global.response.ErrorDetail;
import com.example.backend.global.response.HttpErrorInfo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * GlobalControllerAdvice
 * <p>애플리케이션 전역에서 발생하는 예외를 처리하는 클래스 입니다.</p>
 *
 * @author Kim Dong O
 */
@ControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

	/**
	 * Validation 예외 발생시 처리하는 핸들러
	 * @param ex Exception
	 * @param request HttpServletRequest
	 * @return {@link ResponseEntity<HttpErrorInfo>}
	 */
	@ExceptionHandler({MethodArgumentNotValidException.class})
	public ResponseEntity<HttpErrorInfo> handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex,
		HttpServletRequest request) {
		BindingResult bindingResult = ex.getBindingResult();
		List<ErrorDetail> errors = new ArrayList<>();
		GlobalErrorCode globalErrorCode = GlobalErrorCode.NOT_VALID;

		//Field 에러 처리
		for (FieldError error : bindingResult.getFieldErrors()) {
			ErrorDetail customError = ErrorDetail.of(error.getField(), error.getDefaultMessage());

			errors.add(customError);
		}

		//Object 에러 처리
		for (ObjectError globalError : bindingResult.getGlobalErrors()) {
			ErrorDetail customError = ErrorDetail.of(
				globalError.getObjectName(),
				globalError.getDefaultMessage()
			);

			errors.add(customError);
		}

		return ResponseEntity.status(ex.getStatusCode().value())
			.body(HttpErrorInfo.of(
				GlobalErrorCode.NOT_VALID.getCode(),
				request.getRequestURI(),
				GlobalErrorCode.NOT_VALID.getMessage(),
				errors)
			);
	}

}
