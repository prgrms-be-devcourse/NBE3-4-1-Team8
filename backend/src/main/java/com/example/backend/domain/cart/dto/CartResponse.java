package com.example.backend.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {

    private Long id;
    private Long memberId;
    private String memberNickname;
    private Long productId;
    private String productName;
    private int quantity;
    private int productPrice;
    private String productImgUrl;
}
