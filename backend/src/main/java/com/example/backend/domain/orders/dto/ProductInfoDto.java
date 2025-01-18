package com.example.backend.domain.orders.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@Getter
public class ProductInfoDto {

    private Long id;
    private String name;
    private int price;
    private String imgUrl;
    private int quantity;
}
