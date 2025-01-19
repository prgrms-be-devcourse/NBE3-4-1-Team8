package com.example.backend.domain.cart.repository;

import com.example.backend.domain.cart.entity.Cart;
import com.example.backend.domain.common.Address;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.entity.MemberStatus;
import com.example.backend.domain.member.entity.Role;
import com.example.backend.domain.member.repository.MemberRepository;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.product.repository.ProductRepository;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;
    private Product product;

    @BeforeEach
    void setup() {
        // Given: 회원 및 상품 객체를 생성 후 DB에 저장
        Address address = Address.builder()
                .city("testCity")
                .district("testDistrict")
                .country("testCountry")
                .detail("testDetail")
                .build();

        member = Member.builder()
                .username("testUser")
                .nickname("testNickname")
                .password("!testPassword1234")
                .role(Role.ROLE_USER)
                .memberStatus(MemberStatus.ACTIVE)
                .address(address)
                .build();
        memberRepository.save(member);

        product = Product.builder()
                .name("Test Product")
                .content("Test Product Content")
                .price(1000)
                .quantity(1)
                .imgUrl("http://test.com/image.jpg")
                .build();
        productRepository.save(product);
    }

    @Test
    @DisplayName("장바구니에 해당 상품과 회원이 존재하는지 확인")
    void existsByProductId_IdAndMemberId_Id() {
        // Given
        Cart cart = Cart.builder()
                .member(member)
                .product(product)
                .quantity(1)
                .build();

        cartRepository.save(cart);

        // When
        boolean exists = cartRepository.existsByProductId_IdAndMemberId_Id(
                product.getId(),
                member.getId()
        );

        // Then
        assertTrue(exists);
    }

    @Test
    @DisplayName("회원의 장바구니 목록을 상품 정보와 함께 조회")
    void findAllByMemberWithProducts() {
        // Given
        Cart cart1 = Cart.builder()
                .member(member)
                .product(product)
                .quantity(1)
                .build();

        Product product2 = Product.builder()
                .name("Test Product 2")
                .content("Test Product Content 2")
                .price(2000)
                .quantity(2)
                .imgUrl("http://test.com/image2.jpg")
                .build();
        productRepository.save(product2);

        Cart cart2 = Cart.builder()
                .member(member)
                .product(product2)
                .quantity(2)
                .build();

        cartRepository.saveAll(List.of(cart1, cart2));

        // When
        List<Cart> cartList = cartRepository.findAllByMemberWithProducts(member);

        // Then
        assertThat(cartList).hasSize(2);
        assertThat(cartList).allSatisfy(cart -> {
            assertThat(cart.getMember()).isEqualTo(member);
            assertThat(cart.getProduct()).isNotNull();
            // Hibernate.isInitialized()로 페치 조인이 정상적으로 동작했는지 검증
            assertThat(Hibernate.isInitialized(cart.getProduct())).isTrue();
        });

        // 조회된 상품들의 정보 검증
        assertThat(cartList).extracting("product.name")
                .containsExactlyInAnyOrder("Test Product", "Test Product 2");
        assertThat(cartList).extracting("quantity")
                .containsExactlyInAnyOrder(1, 2);
    }
}
