package com.example.backend.domain.product.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * ProductErrorCode
 * 상품 비지니스 로직에서 발생하는 예외 코드를 정의하는 Enum 클래스입니다.
 *
 * @author 100minha
 */
@AllArgsConstructor
@Getter
public enum ProductErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다.", "404"),
    EXISTS_NAME(HttpStatus.BAD_REQUEST, "400-2", "중복된 상품 이름입니다.");

    final HttpStatus httpStatus;
    final String message;
    final String code;
}
