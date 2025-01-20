package com.example.backend.domain.cart.exception;

import org.springframework.http.HttpStatus;

public class CartException extends RuntimeException {
    private final CartErrorCode cartErrorCode;

    public CartException(CartErrorCode cartErrorCode) {
        super(cartErrorCode.message);
        this.cartErrorCode = cartErrorCode;
    }

    public HttpStatus getStatus() {
        return cartErrorCode.httpStatus;
    }

    public String getCode() {
        return cartErrorCode.code;
    }
}