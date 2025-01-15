package com.example.backend.domain.product.exception;

import org.springframework.http.HttpStatus;

/**
 * ProductException
 * 상품 관련 예외 처리 클래스
 *
 * @author 100minha
 */
public class ProductException extends RuntimeException {
    private final ProductErrorCode productErrorCode;

    public ProductException(ProductErrorCode productErrorCode) {
        super(productErrorCode.getMessage());
        this.productErrorCode = productErrorCode;
    }

    public HttpStatus getStatus() {
        return productErrorCode.httpStatus;
    }

    public String getCode() {
        return productErrorCode.code;
    }
}
