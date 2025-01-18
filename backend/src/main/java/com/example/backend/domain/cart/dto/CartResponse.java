package com.example.backend.domain.cart.dto;

import lombok.Builder;

@Builder
public record CartResponse(

        Long id,

        Long memberId,
        String memberNickname,

        Long productId,
        String productName,
        int quantity,
        int productPrice,
        String productImgUrl
) {}
