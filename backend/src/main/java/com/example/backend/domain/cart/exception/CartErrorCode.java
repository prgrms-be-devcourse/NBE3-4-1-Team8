package com.example.backend.domain.cart.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum CartErrorCode {
    INVALID_MEMBER_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 회원 ID입니다.", "400"),
    INVALID_PRODUCT_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 상품 ID입니다.", "400"),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다.", "400");


    final HttpStatus httpStatus;
    final String message;
    final String code;
}