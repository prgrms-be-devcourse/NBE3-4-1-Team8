package com.example.backend.domain.orders.exception;

import org.springframework.http.HttpStatus;

public class OrdersException extends RuntimeException {

    private OrdersErrorCode ordersErrorCode;

    public OrdersException(OrdersErrorCode ordersErrorCode) {
        super(ordersErrorCode.message);
        this.ordersErrorCode = ordersErrorCode;
    }

    public HttpStatus getStatus() {
        return ordersErrorCode.httpStatus;
    }

    public String getCode() {
        return ordersErrorCode.code;
    }
}
