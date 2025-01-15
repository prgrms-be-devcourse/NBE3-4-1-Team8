package com.example.backend.domain.product.service;

import com.example.backend.domain.product.dto.ProductForm;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * ProductServiceImpl
 * 상품 관련 서비스 로직 구현
 * @author 100minha
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public void create(ProductForm productForm) {

        Product product = Product.builder()
                .name(productForm.name())
                .content(productForm.content())
                .price(productForm.price())
                .imgUrl(productForm.imgUrl())
                .quantity(productForm.quantity())
                .build();

        productRepository.save(product);
    }

}
