package com.example.backend.domain.product.dto;

import lombok.Builder;

/**
 * ProductResponse
 * 상품 조회 시 사용하는 DTO
 * @author 100minha
 */
@Builder
public record ProductResponse(
        Long id,
        String name,
        String content,
        int price,
        String imgUrl
) {

}
