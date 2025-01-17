package com.example.backend.global.advice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.backend.domain.member.exception.MemberException;
import com.example.backend.domain.product.exception.ProductException;
import com.example.backend.global.auth.exception.AuthException;
import com.example.backend.global.exception.GlobalErrorCode;
import com.example.backend.global.exception.GlobalException;
import com.example.backend.global.response.ErrorDetail;
import com.example.backend.global.response.HttpErrorInfo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
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
	 *
	 * @param ex      Exception
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

	/**
	 * Validation 예외 발생시 처리하는 핸들러
	 *
	 * @param ex      Exception
	 * @param request HttpServletRequest
	 * @return {@link ResponseEntity<HttpErrorInfo>}
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<HttpErrorInfo> handleConstraintViolationException(ConstraintViolationException ex,
		HttpServletRequest request) {
		Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
		List<ErrorDetail> errorDetails = new ArrayList<>();

		for (ConstraintViolation<?> constraintViolation : constraintViolations) {
			errorDetails.add(ErrorDetail.of(
				constraintViolation.getPropertyPath().toString(),
				constraintViolation.getMessage()
			));
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(HttpErrorInfo.of(GlobalErrorCode.NOT_VALID.getCode(), request.getRequestURI(),
				GlobalErrorCode.NOT_VALID.getMessage(),
				errorDetails)
			);
	}

	/**
	 * Member 예외 발생시 처리하는 핸들러
	 *
	 * @param ex      Exception
	 * @param request HttpServletRequest
	 * @return {@link ResponseEntity<HttpErrorInfo>}
	 */
	@ExceptionHandler(MemberException.class)
	public ResponseEntity<HttpErrorInfo> handlerMemberException(MemberException ex, HttpServletRequest request) {
		log.error("GlobalControllerAdvice={}", ex);
		return ResponseEntity.status(ex.getStatus())
			.body(HttpErrorInfo.of(ex.getCode(), request.getRequestURI(), ex.getMessage()));
	}

	@ExceptionHandler(GlobalException.class)
	public ResponseEntity<HttpErrorInfo> handlerGlobalException(GlobalException ex, HttpServletRequest request) {
		log.error("GlobalControllerAdvice={}", ex);
		return ResponseEntity.status(ex.getStatus())
			.body(HttpErrorInfo.of(ex.getCode(), request.getRequestURI(), ex.getMessage()));
	}

	@ExceptionHandler(AuthException.class)
	public ResponseEntity<HttpErrorInfo> handlerAuthException(AuthException ex, HttpServletRequest request) {
		log.error("AuthControllerAdvice={}", ex);
		return ResponseEntity.status(ex.getStatus())
			.body(HttpErrorInfo.of(ex.getCode(), request.getRequestURI(), ex.getMessage()));
	}

	/**
	 * Product 예외 발생시 처리하는 핸들러
	 *
	 * @param ex      ProductException
	 * @param request HttpServletRequest
	 * @return {@link ResponseEntity<HttpErrorInfo>}
	 */
	@ExceptionHandler(ProductException.class)
	public ResponseEntity<HttpErrorInfo> handlerProductException(ProductException ex, HttpServletRequest request) {
		log.info("GlobalControllerAdvice={}", ex);
		return ResponseEntity.status(ex.getStatus())
			.body(HttpErrorInfo.of(ex.getCode(), request.getRequestURI(), ex.getMessage()));
	}

	/**
	 * @param ex      HttpMessageNotReadableException
	 * @param request HttpServletRequest
	 * @return {@link ResponseEntity<HttpErrorInfo>}
	 * @RequestBody에서 파싱할 수 없는 값 들어올 시 발생하는 예외 처리 핸들러
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<HttpErrorInfo> handlerHttpMessageNotReadableException(HttpMessageNotReadableException ex,
		HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(HttpErrorInfo.of("400", request.getRequestURI(), ex.getMessage()));
	}
}
