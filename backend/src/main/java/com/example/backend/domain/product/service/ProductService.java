package com.example.backend.domain.product.service;

import com.example.backend.domain.product.dto.ProductForm;

/**
 * ProductService
 * 상품 관련 서비스 로직 추상화
 * @author 100mi
 */
public interface ProductService {
    // 상품 등록
    void create(ProductForm productForm);
}