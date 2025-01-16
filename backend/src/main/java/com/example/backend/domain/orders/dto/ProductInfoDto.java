package com.example.backend.domain.orders.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductInfoDto {

    private Long id;
    private String name;
    private int price;
    private String ima_url;
    private int quantity;
}
