package com.example.backend.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponseDto {
    private Long id; // 장바구니 ID
    private Long memberId; // 회원 ID
    private String memberUsername; // 회원 이름 (옵션: 편의를 위해 추가)
    private Long productId; // 상품 ID
    private String productName; // 상품 이름
    private int quantity; // 상품 수량
    private int productPrice; // 상품 가격
    private int totalPrice; // 상품 총 가격 (수량 * 단가)
}
