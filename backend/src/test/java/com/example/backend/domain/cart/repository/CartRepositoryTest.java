package com.example.backend.domain.cart.repository;

import com.example.backend.domain.cart.entity.Cart;
import com.example.backend.domain.common.Address;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.repository.MemberRepository;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static com.example.backend.domain.member.entity.Role.ROLE_USER;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    private Cart cart;

    @BeforeEach
    void setUp() {
        // Member 객체 생성
        member = Member.builder()
                .username("testUser@gmail.com")
                .nickname("testNickname")
                .password("testPassword")
                .role(ROLE_USER)
                .address(new Address("testCity", "testDistrict", "testCountry", "testDetail"))
                .build();

        // Product 객체 생성
        product = new Product("Product1", "content", 1000, "imageURL", 10);

        // Member와 Product 저장
        memberRepository.save(member);
        productRepository.save(product);

        // Cart 객체 생성 후 저장
        cart = Cart.builder()
                .member(member)  // Member 객체 설정
                .product(product)  // Product 객체 설정
                .quantity(1)  // 수량 설정
                .build();

        cartRepository.save(cart);
    }

    @Test
    @DisplayName("상품과 회원이 장바구니에 존재할 경우")
    void testExistsByProductIdAndMemberId_whenExists() {
        // 상품과 회원이 장바구니에 존재할 때
        boolean exists = cartRepository.existsByProductId_IdAndMemberId_Id(product.getId(), member.getId());
        assertTrue(exists, "주어진 상품과 회원에 대한 장바구니가 존재합니다.");
    }

    @Test
    @DisplayName("상품과 회원이 장바구니에 존재하지 않을 경우")
    void testExistsByProductIdAndMemberId_whenNotExists() {
        Member newMember = Member.builder()
                .username("newUsername")
                .nickname("newNickname")
                .password("newPassword")
                .role(ROLE_USER)
                .address(new Address("newCity", "newDistrict", "newCountry", "newDetail"))
                .build();
        memberRepository.save(newMember);

        boolean exists = cartRepository.existsByProductId_IdAndMemberId_Id(product.getId(), newMember.getId());
        assertFalse(exists, "주어진 상품과 회원에 대한 장바구니가 존재하지 않습니다.");
    }
}
