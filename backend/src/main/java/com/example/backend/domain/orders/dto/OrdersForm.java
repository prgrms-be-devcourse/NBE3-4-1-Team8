package com.example.backend.domain.orders.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

import static com.example.backend.global.validation.ValidationGroups.*;

public record OrdersForm(
        @NotNull(message = "회원 ID는 필수입니다.", groups = NotNullGroup.class)
        Long memberId,

        @NotBlank(message = "도시는 필수입니다.", groups = NotNullGroup.class)
        String city,

        @NotBlank(message = "구는 필수입니다.", groups = NotNullGroup.class)
        String district,

        @NotBlank(message = "국가는 필수입니다.", groups = NotNullGroup.class)
        String country,

        @NotBlank(message = "상세 주소는 필수입니다.", groups = NotNullGroup.class)
        String detail,

        @NotEmpty(message = "상품 주문 리스트는 비어 있을 수 없습니다.", groups = NotEmptyGroup.class)
        @Size(min = 1, max = 20, message = "상품 리스트는 1 ~ 20개 사이여야 합니다.", groups = SizeGroup.class)
        List<@Valid ProductOrdersRequest> productOrdersRequestList
) {
    public record ProductOrdersRequest(
            @NotNull(message = "상품 ID는 필수입니다.", groups = NotNullGroup.class)
            Long productId, // 상품 ID

            @Min(value = 1, message = "수량은 1 이상이어야 합니다.", groups = MinGroup.class)
            @Max(value = 99999999, message = "수량은 9,999,999개 이하여야 합니다.", groups = MaxGroup.class)
            int quantity // 수량
    ) {}
}