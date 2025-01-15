package com.example.backend.domain.product.repository;

import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.product.repository.ProductRepository;
import com.example.backend.global.config.JpaAuditingConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ProductReposiotryTest
 * ProductRepository 단위 테스트 진행 코드
 * @author 100minha
 */
@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품 저장 테스트")
    void saveSuccessTest() {
        // given
        Product productForm = Product.builder()
                .name("상품 이름")
                .content("상품 설명")
                .price(10000)
                .imgUrl("test.jpg.com")
                .quantity(100)
                .build();

        // when
        productRepository.save(productForm);
        Optional<Product> optionalProduct = productRepository.findById(1L);

        // then
        assertThat(optionalProduct.isPresent()).isTrue();
        Product product = optionalProduct.get();
        assertThat(productRepository.count()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo(productForm.getName());
    }

}
