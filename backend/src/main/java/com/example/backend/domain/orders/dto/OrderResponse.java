package com.example.backend.domain.orders.dto;


import com.example.backend.domain.orders.status.DeliveryStatus;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * 주문 단건 조회 시 사용되는 dto
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderResponse {

    private Long id;
    private List<ProductInfoDto> products;
    private int totalPrice;
    private DeliveryStatus status;
    private ZonedDateTime createAt;
    private ZonedDateTime modifiedAt;

}
