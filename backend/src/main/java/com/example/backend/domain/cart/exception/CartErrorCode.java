package com.example.backend.domain.cart.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum CartErrorCode {
    ALREADY_EXISTS_IN_CART(HttpStatus.BAD_REQUEST, "이미 장바구니에 추가된 상품입니다.", "400-1"),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST,"상품을 최소 1개 이상 추가하여야 합니다." ,"400-2"),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST,"재고가 부족합니다." ,"400-3");

    final HttpStatus httpStatus;
    final String message;
    final String code;
}