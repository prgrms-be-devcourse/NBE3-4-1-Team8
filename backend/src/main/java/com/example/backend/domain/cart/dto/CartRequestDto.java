package com.example.backend.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRequestDto {
    private Long memberId; // 어떤 회원이 요청했는지
    private Long productId; // 어떤 상품을 추가/수정할지
    private int quantity; // 상품의 수량
}
