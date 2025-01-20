package com.example.backend.domain.product.converter;

import com.example.backend.domain.product.dto.ProductForm;
import com.example.backend.domain.product.dto.ProductResponse;
import com.example.backend.domain.product.entity.Product;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * ProductConverter
 * 엔티티, DTO간의 변환 메서드를 관리하는 클래스
 *
 * @author 100minha
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductConverter {

    public static Product from(ProductForm productForm) {

        return Product.builder()
                .name(productForm.name())
                .content(productForm.content())
                .price(productForm.price())
                .imgUrl(productForm.imgUrl())
                .quantity(productForm.quantity())
                .build();
    }

    public static ProductResponse from(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getContent(),
                product.getPrice(),
                product.getImgUrl()
        );
    }
}
