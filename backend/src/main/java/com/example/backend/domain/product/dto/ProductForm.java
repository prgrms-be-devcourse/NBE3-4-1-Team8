package com.example.backend.domain.product.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import static com.example.backend.global.validation.ValidationGroups.*;

/**
 * ProductForm
 * 상품 등록 및 수정 시 사용하는 DTO
 * @author 100
 */
@Builder
public record ProductForm(
        @NotBlank(message = "상품 이름은 공백일 수 없습니다.", groups = NotBlankGroup.class)
        @Length(min = 2, max = 50, message = "상품 이름은 2자 이상 50자 이하여야 합니다.", groups = SizeGroup.class)
        String name,

        @NotBlank(message = "상품 설명은 공백일 수 없습니다.", groups = NotBlankGroup.class)
        String content,

        @Min(value = 100, message = "상품 가격은 100원 이상이어야 합니다.", groups = MinGroup.class)
        @Max(value = 9999999, message = "상품 가격은 9,999,999원 이하여야 합니다.", groups = MaxGroup.class)
        int price,

        String imgUrl,

        @Min(value = 0, message = "상품 수량은 0 이상이어야 합니다.", groups = MaxGroup.class)
        int quantity
) {

}
