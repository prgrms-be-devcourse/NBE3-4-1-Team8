package com.example.backend.domain.product.repository;

import com.example.backend.domain.orders.dto.OrdersForm;
import com.example.backend.domain.product.dto.ProductResponse;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.productOrders.entity.ProductOrders;
import com.example.backend.domain.productOrders.repository.ProductOrdersRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ProductReposiotryTest
 * ProductRepository 단위 테스트 진행 코드
 *
 * @author 100minha
 */
@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductOrdersRepository productOrdersRepository;

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
    @DisplayName("상품 이름 유일성 검사(중복) 테스트")
    void existsByNameTest() {
        //given
        //when
        Boolean isExists = productRepository.existsByName(name1);

        //then
        assertThat(isExists).isTrue();
    }

    @Test
    @DisplayName("상품 이름 유일성 검사(유일) 테스트")
    void notExistsByNameTest() {
        //given
        String name = "Unique Product Name";

        //when
        Boolean isExists = productRepository.existsByName(name);

        //then
        assertThat(isExists).isFalse();
    }

    @Test
    @DisplayName("상품 특정 id 제외 이름 유일성 검사 테스트")
    void existsByNameAndIdNotTest() {
        //given
        String name2 = "Test Product Name2";
        String uniqueName = "Unique Product Name";

        Product product2 = Product.builder()
                .name(name2)
                .build();
        productRepository.save(product2);
        Long id = 2L;

        //when
        Boolean isExists1 = productRepository.existsByNameAndIdNot(name2, id);  // 수정 목표 상품 id의 name
        Boolean isExists2 = productRepository.existsByNameAndIdNot(name1, id);  // 다른 상품의 name
        Boolean isExists3 = productRepository.existsByNameAndIdNot(uniqueName, id); // 수정 했을 때 아예 다른 name

        //then
        assertThat(isExists1).isFalse();
        assertThat(isExists2).isTrue();
        assertThat(isExists3).isFalse();
    }

    @Test
    @DisplayName("상품 단건 조회(Entity) 테스트")
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
    @DisplayName("상품 단건 조회(DTO) 테스트")
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

    @Test
    @DisplayName("상품 다건 조회 테스트")
    void findAllPagedTest() {
        // given
        PageRequest pageRequest1 = PageRequest.of(0, 10);   // 1페이지
        PageRequest pageRequest2 = PageRequest.of(1, 10);   // 2페이지

        for (int i = 1; i <= 15; i++) {
            productRepository.save(Product.builder()    // 15 + setUp()에서 +1
                    .build());
        }

        // when
        Page<ProductResponse> productResponsePage1 = productRepository.findAllPaged(pageRequest1);
        Page<ProductResponse> productResponsePage2 = productRepository.findAllPaged(pageRequest2);

        // then
        assertThat(productResponsePage1).isNotNull();
        assertThat(productResponsePage1.getTotalPages()).isEqualTo(2);
        assertThat(productResponsePage1.getTotalElements()).isEqualTo(16);

        assertThat(productResponsePage1.getNumberOfElements()).isEqualTo(10);
        assertThat(productResponsePage2.getNumberOfElements()).isEqualTo(6);

    }

    @Test
    @DisplayName("상품 삭제 성공 테스트")
    void deleteTest() {
        //given
        Long id = 1L;
        Product product = productRepository.findById(id).get();

        //when
        productRepository.delete(product);

        //then
        assertThat(productRepository.findById(id).isPresent()).isFalse();
    }

    @Test
    @DisplayName("상품 주문 내역 존재 여부 검증 테스트")
    void existsProductOrdersByProductIdTest() {
        //given
        Product product1 = productRepository.findById(1L).get();

        ProductOrders productOrders = ProductOrders.create()
                .product(product1)
                .build();

        productOrdersRepository.save(productOrders);

        //when
        boolean isExists1 = productOrdersRepository.existsByProductId(1L);
        boolean isExists2 = productOrdersRepository.existsByProductId(2L);

        //then
        assertThat(isExists1).isTrue();
        assertThat(isExists2).isFalse();
    }
}
