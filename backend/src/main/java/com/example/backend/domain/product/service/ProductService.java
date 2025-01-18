package com.example.backend.domain.product.service;

import com.example.backend.domain.product.converter.ProductConverter;
import com.example.backend.domain.product.dto.ProductForm;
import com.example.backend.domain.product.dto.ProductResponse;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.product.exception.ProductErrorCode;
import com.example.backend.domain.product.exception.ProductException;
import com.example.backend.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public Page<ProductResponse> findAllPaged(int page) {

        Sort sortByNameAsc = Sort.by(Sort.Order.asc("name"));
        Pageable pageable = PageRequest.of(page, 10, sortByNameAsc);

        Page<ProductResponse> productResponsePage = productRepository.findAllPaged(pageable);

        if(productResponsePage.isEmpty()) {
            throw new ProductException(ProductErrorCode.NOT_FOUND);
        }

        return productResponsePage;
    }

    @Transactional
    public void create(ProductForm productForm) {

        productRepository.save(ProductConverter.from(productForm));
    }

    @Transactional
    public void modify(Long id, ProductForm productForm) {

        findById(id).modify(productForm);
    }
}
