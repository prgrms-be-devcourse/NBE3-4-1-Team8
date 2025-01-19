package com.example.backend.domain.orders.dto;


import com.example.backend.domain.orders.status.DeliveryStatus;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;


@Builder
public record OrdersResponse(
        Long id,
        List<ProductInfoDto> products,
        int totalPrice,
        DeliveryStatus status,
        ZonedDateTime createAt,
        ZonedDateTime modifiedAt
) {}