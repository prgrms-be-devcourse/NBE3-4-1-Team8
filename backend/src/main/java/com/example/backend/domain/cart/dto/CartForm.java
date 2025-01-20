package com.example.backend.domain.cart.dto;

import com.example.backend.global.validation.ValidationSequence;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartForm(
        @NotNull(message = "상품 ID는 필수 값입니다.", groups = ValidationSequence.class)
        Long productId,
        @Min(value = 1, message = "상품 수량은 최소 1개 이상이어야 합니다.", groups = ValidationSequence.class)
        int quantity
) {
}