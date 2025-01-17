package com.example.backend.domain.product.repository;

import com.example.backend.domain.product.dto.ProductForm;
import com.example.backend.domain.product.dto.ProductResponse;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.product.repository.ProductRepository;
import com.example.backend.global.config.JpaAuditingConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
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

    @PersistenceContext
    private EntityManager em;

    private final String name1 = "Test Product Name";
    private final String content1 = "Test Product Description";
    private final int price1 = 1000;
    private final String imgUrl1 = "Test Product Image";
    private final int quantity1 = 10;

    private Long productId;

    /**
     * id 1부터 시작하도록 초기화 후 상품 생성
     */
    @BeforeEach
    void setUp() {
        em.createNativeQuery("ALTER TABLE product ALTER COLUMN id RESTART WITH 1").executeUpdate();
        Product product = Product.builder()
                .name(name1)
                .content(content1)
                .price(price1)
                .imgUrl(imgUrl1)
                .quantity(quantity1)
                .build();

        productRepository.save(product);
    }

    @Test
    @DisplayName("상품 저장 테스트")
    void saveTest() {
        // given
        // when
        Optional<Product> optionalProduct = productRepository.findById(1L);

        // then
        assertThat(optionalProduct.isPresent()).isTrue();
        assertThat(optionalProduct.get()).isInstanceOf(Product.class);
    }

    @Test
    @DisplayName("상품 조회(Entity) 테스트")
    void findByIdTest() {
        // given
        // when
        Optional<Product> optionalProduct = productRepository.findById(1L);

        // then
        assertThat(optionalProduct.isPresent()).isTrue();
        Product product = optionalProduct.get();
        assertThat(product.getName()).isEqualTo(this.name1);
        assertThat(product.getContent()).isEqualTo(this.content1);
        assertThat(product.getPrice()).isEqualTo(this.price1);
        assertThat(product.getImgUrl()).isEqualTo(this.imgUrl1);
        assertThat(product.getQuantity()).isEqualTo(this.quantity1);
    }

    @Test
    @DisplayName("상품 조회(DTO) 테스트")
    void findProductResponseByIdTest() {
        // given
        // when
        Optional<ProductResponse> optionalProductResponse = productRepository.findProductResponseById(1L);

        // then
        assertThat(optionalProductResponse.isPresent()).isTrue();
        ProductResponse productResponse = optionalProductResponse.get();
        assertThat(productResponse.name()).isEqualTo(this.name1);
        assertThat(productResponse.content()).isEqualTo(this.content1);
        assertThat(productResponse.price()).isEqualTo(this.price1);
        assertThat(productResponse.imgUrl()).isEqualTo(this.imgUrl1);
    }

}
