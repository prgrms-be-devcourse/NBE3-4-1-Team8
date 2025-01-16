package com.example.backend.domain.cart.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum CartErrorCode {
    INVALID_MEMBER_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 회원 ID입니다.", "400"),
    INVALID_PRODUCT_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 상품 ID입니다.", "400"),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다.", "400"),
    ALREADY_EXISTS_IN_CART(HttpStatus.BAD_REQUEST, "이미 장바구니에 추가된 상품입니다.", "400"),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST,"상품을 최소 1개 이상 추가하여야 합니다." ,"400");

    final HttpStatus httpStatus;
    final String message;
    final String code;
}