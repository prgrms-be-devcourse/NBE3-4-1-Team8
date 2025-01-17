package com.example.backend.domain.cart.service;

import com.example.backend.domain.cart.dto.CartForm;
import com.example.backend.domain.cart.entity.Cart;
import com.example.backend.domain.cart.repository.CartRepository;
import com.example.backend.domain.common.Address;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.entity.Role;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.global.auth.model.CustomUserDetails;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @PersistenceContext
    private EntityManager em;

    private Member testMember; // 테스트에서 사용할 Member 객체

    @BeforeEach
    void setUp() {
        // 테스트용 Member 생성
        testMember = Member.builder()
                .username("testUser")
                .nickname("testNick")
                .password("password")
                .role(Role.ROLE_USER)
                .address(new Address(
                        "testCity",
                        "testDistrict",
                        "testCountry",
                        "testDetail"))
                .createdAt(ZonedDateTime.now())
                .modifiedAt(ZonedDateTime.now())
                .build();

        // Member 저장 (ID 자동 생성)
        em.persist(testMember);
        em.flush();
        em.clear();

        // 테스트용 Product 생성
        Product testProduct = Product.builder()
                .name("Test Product")
                .content("Test Description")
                .price(1000)
                .imgUrl("http://testproduct.com/image.jpg")
                .quantity(10)
                .build();

        // Product 저장
        em.persist(testProduct);
        em.flush();
        em.clear();

        // SecurityContext 설정
        CustomUserDetails customUserDetails = new CustomUserDetails(testMember);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities()
        ));
        SecurityContextHolder.setContext(context);

        // 데이터베이스 초기화
        em.createNativeQuery("ALTER TABLE cart ALTER COLUMN id RESTART WITH 1;").executeUpdate();

        // 테스트 데이터 추가 (유효한 productId 사용)
        int quantity1 = 1;
        cartService.addCartItem(new CartForm(
                testMember.getId(),
                testProduct.getId(), // 저장한 testProduct의 ID 사용
                quantity1
        ));
    }


    @Test
    @DisplayName("장바구니 상품 추가 테스트")
    void addCartItem() {
        // given
        Product testProduct2 = Product.builder()
                .name("Another Product")
                .content("Another Description")
                .price(1500)
                .imgUrl("http://anotherproduct.com/image.jpg")
                .quantity(15)
                .build();
        em.persist(testProduct2);
        em.flush();
        em.clear();

        CartForm cartForm = new CartForm(
                testMember.getId(),
                testProduct2.getId(), // 존재하는 testProduct2의 ID 사용
                2
        );

        // when
        Long cartId = cartService.addCartItem(cartForm);
        Cart savedCart = cartRepository.findById(cartId).orElse(null);

        // then
        assertThat(savedCart).isNotNull();
        assertThat(savedCart.getMemberId().getId()).isEqualTo(testMember.getId());
        assertThat(savedCart.getProductId().getId()).isEqualTo(testProduct2.getId());
        assertThat(savedCart.getQuantity()).isEqualTo(2);
    }

}
