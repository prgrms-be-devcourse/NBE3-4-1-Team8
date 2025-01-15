package com.example.backend.domain.orders.dto;


import com.example.backend.domain.orders.status.DeliveryStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderResponse {

    private Long id;
    private List<ProductInfoDto> products;
    private int totalPrice;
    private DeliveryStatus status;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;

}
