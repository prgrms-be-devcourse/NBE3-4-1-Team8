package com.example.backend.domain.product.service;

import com.example.backend.domain.product.converter.ProductConverter;
import com.example.backend.domain.product.dto.ProductForm;
import com.example.backend.domain.product.dto.ProductResponse;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.product.exception.ProductErrorCode;
import com.example.backend.domain.product.exception.ProductException;
import com.example.backend.domain.product.repository.ProductRepository;
import com.example.backend.domain.productOrders.repository.ProductOrdersRepository;
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
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductOrdersRepository productOrdersRepository;

    @Transactional(readOnly = true)
    public Product findById(Long id) {

        return productRepository.findById(id).orElseThrow(()
                -> new ProductException(ProductErrorCode.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public ProductResponse findProductResponseById(Long id) {

        return productRepository.findProductResponseById(id).orElseThrow(()
                -> new ProductException(ProductErrorCode.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findAllPaged(int page) {

        Sort sortByNameAsc = Sort.by(Sort.Order.asc("name"));
        Pageable pageable = PageRequest.of(page, 10, sortByNameAsc);

        Page<ProductResponse> productResponsePage = productRepository.findAllPaged(pageable);

        if(productResponsePage.isEmpty()) {
            throw new ProductException(ProductErrorCode.NOT_FOUND);
        }

        return productResponsePage;
    }

    /**
     * 상품 등록 시 이름 중복 검증 메서드
     * @param name
     */
    private void existsProduct(String name) {

        if(productRepository.existsByName(name)) {
            throw new ProductException(ProductErrorCode.EXISTS_NAME);
        }
    }

    /**
     * 상품 수정 시 이름 중복 검증 메서드
     * 수정시엔 해당 상품의 기존 이름은 중복 검증에서 제외
     * @param id
     * @param name
     */
    private void existsProduct(Long id, String name) {

        if(productRepository.existsByNameAndIdNot(name, id)) {
            throw new ProductException(ProductErrorCode.EXISTS_NAME);
        }
    }

    @Transactional
    public void create(ProductForm productForm) {

        existsProduct(productForm.name());
        productRepository.save(ProductConverter.from(productForm));
    }

    @Transactional
    public void modify(Long id, ProductForm productForm) {

        existsProduct(id, productForm.name());
        findById(id).modify(productForm);
    }

    @Transactional
    public void delete(Long id) {

        if(productOrdersRepository.existsByProductId(id)) {
            throw new ProductException(ProductErrorCode.EXISTS_ORDER_HISTORY);
        }

        productRepository.delete(findById(id));
    }
}
