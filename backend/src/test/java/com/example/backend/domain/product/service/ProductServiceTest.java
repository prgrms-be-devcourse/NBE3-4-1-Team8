package com.example.backend.domain.product.service;

import com.example.backend.domain.product.dto.ProductForm;
import com.example.backend.domain.product.dto.ProductResponse;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.global.config.JpaAuditingConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ProductServiceTest
 * <p></p>
 *
 * @author 100mi
 */
@SpringBootTest
@Import(JpaAuditingConfig.class)
@ActiveProfiles("test")
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @PersistenceContext
    private EntityManager em;

    private final String name1 = "Test Product Name";
    private final String content1 = "Test Product Description";
    private final int price1 = 1000;
    private final String imgUrl1 = "Test Product Image";
    private final int quantity1 = 10;

    private Long productId;

    @BeforeEach
    void setUp() {
        em.createNativeQuery("ALTER TABLE product ALTER COLUMN id RESTART WITH 1").executeUpdate();

        productService.create(new ProductForm(
                name1,
                content1,
                price1,
                imgUrl1,
                quantity1
        ));

    }

    @Test
    void findById() {
        // given
        // when
        Product product = productService.findById(1L);

        // then
        assertThat(product.getName()).isEqualTo(this.name1) ;
    }

    @Test
    void findProductResponseById() {
        // given
        // when
        ProductResponse productResponse = productService.findProductResponseById(1L);

        // then
        assertThat(productResponse.name()).isEqualTo(this.name1) ;
    }
}