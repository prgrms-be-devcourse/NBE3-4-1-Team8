package com.example.backend.domain.product.dto;

import com.example.backend.domain.product.entity.Product;

/**
 * ProductResponse
 * 상품 조회 시 사용하는 DTO
 * @author 100minha
 */
public record ProductResponse(
        Long Id,
        String name,
        String content,
        int price,
        String imgUrl,
        int quantity
) {

    public static ProductResponse of(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getContent(),
                product.getPrice(),
                product.getImgUrl(),
                product.getQuantity()
        );
    }
}
