package com.example.backend.domain.cart.dto;

import lombok.Builder;

@Builder
public record CartResponse(
        Long id,
        String productName,
        int quantity,
        int productPrice,
        int totalPrice,
        String productImgUrl
) {}
