package com.example.backend.domain.product.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

/**
 * ProductForm
 * 상품 등록 및 수정 시 사용하는 DTO
 * @author 100
 */
public record ProductForm(
        @NotBlank(message = "상품 이름은 공백일 수 없습니다.")
        @Length(min = 2, max = 50, message = "상품 이름은 2자 이상 50자 이하여야 합니다.")
        String name,

        @NotBlank(message = "상품 설명은 공백일 수 없습니다.")
        String content,

        @Min(value = 100, message = "상품 가격은 100원 이상이어야 합니다.")
        @Max(value = 9999999, message = "상품 가격은 9,999,999원 이하여야 합니다.")
        int price,

        String imgUrl,

        @Min(value = 0, message = "상품 수량은 0 이상이어야 합니다.")
        int quantity
) {

}
