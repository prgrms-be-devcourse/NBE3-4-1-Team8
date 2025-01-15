package com.example.backend.domain.product.service;

import com.example.backend.domain.product.dto.ProductForm;
import com.example.backend.domain.product.dto.ProductResponse;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.product.exception.ProductErrorCode;
import com.example.backend.domain.product.exception.ProductException;
import com.example.backend.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public Product findById(Long id) {

        return productRepository.findById(id).orElseThrow(()
                -> new ProductException(ProductErrorCode.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findProductResponseById(Long id) {

        return ProductResponse.of(this.findById(id));
    }

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
