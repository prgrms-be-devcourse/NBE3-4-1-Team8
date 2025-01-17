package com.example.backend.domain.product.service;

import com.example.backend.domain.product.dto.ProductForm;
import com.example.backend.domain.product.dto.ProductResponse;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.product.exception.ProductErrorCode;
import com.example.backend.domain.product.exception.ProductException;
import com.example.backend.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ProductServiceImpl
 * 상품 관련 서비스 로직 구현
 * @author 100minha
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public Product findById(Long id) {

        return productRepository.findById(id).orElseThrow(()
                -> new ProductException(ProductErrorCode.NOT_FOUND));
    }

    public ProductResponse findProductResponseById(Long id) {

        return productRepository.findProductResponseById(id).orElseThrow(()
                -> new ProductException(ProductErrorCode.NOT_FOUND));
    }

    public Page<ProductResponse> findAllPage(int page) {

        PageRequest pageRequest = PageRequest.of(page, 10);
        return productRepository.findAllPaged(pageRequest);
    }

    @Transactional
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
