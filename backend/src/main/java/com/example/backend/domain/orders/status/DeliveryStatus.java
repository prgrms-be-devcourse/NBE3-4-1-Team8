package com.example.backend.domain.orders.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DeliveryStatus {

    READY("배송 준비중"),
    SHIPPED("배송 시작"),
    CANCEL("주문 취소");

    private String description;

}
