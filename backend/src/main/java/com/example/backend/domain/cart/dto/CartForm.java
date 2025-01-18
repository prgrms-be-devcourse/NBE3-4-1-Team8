package com.example.backend.domain.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartForm(
        @NotNull(message = "회원 ID는 필수 값입니다.")
        Long memberId,
        @NotNull(message = "상품 ID는 필수 값입니다.")
        Long productId,
        @Min(value = 1, message = "상품 수량은 최소 1개 이상이어야 합니다.")
        int quantity
) {
}