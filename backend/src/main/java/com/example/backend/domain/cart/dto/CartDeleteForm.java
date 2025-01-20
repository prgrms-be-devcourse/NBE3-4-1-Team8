package com.example.backend.domain.cart.dto;

import com.example.backend.global.validation.ValidationSequence;
import jakarta.validation.constraints.NotNull;

public record CartDeleteForm(
        @NotNull(message = "상품 ID는 필수 값입니다.", groups = ValidationSequence.class)
        Long productId
) { }
