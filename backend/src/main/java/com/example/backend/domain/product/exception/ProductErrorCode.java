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
    INSUFFICIENT_QUANTITY(HttpStatus.BAD_REQUEST, "상품 재고가 부족합니다.", "400-1"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다.", "404"),
    CONFLICT(HttpStatus.CONFLICT, "현재 다른 사용자가 해당 상품을 처리 중입니다. 잠시 후 다시 시도해 주세요.", "409-1");

    final HttpStatus httpStatus;
    final String message;
    final String code;
}
