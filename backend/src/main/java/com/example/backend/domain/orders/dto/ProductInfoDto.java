package com.example.backend.domain.orders.dto;

import lombok.*;

@Builder

public record ProductInfoDto (
        Long id,
        String name,
        int price,
        String imgUrl,
        int quantity
){


}
