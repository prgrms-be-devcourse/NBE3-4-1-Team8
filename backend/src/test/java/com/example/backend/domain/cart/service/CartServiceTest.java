package com.example.backend.domain.cart.service;

import com.example.backend.domain.cart.converter.CartConverter;
import com.example.backend.domain.cart.dto.CartForm;
import com.example.backend.domain.cart.entity.Cart;
import com.example.backend.domain.cart.exception.CartException;
import com.example.backend.domain.cart.repository.CartRepository;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.entity.MemberStatus;
import com.example.backend.domain.member.entity.Role;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.product.service.ProductService;
import com.example.backend.global.auth.exception.AuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductService productService;

    @Mock
    private CartConverter cartConverter;

    private Member member;
    private Product product;
    private CartForm cartForm;
    private Cart cart;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .username("testUser")
                .password("!testPassword1234")
                .role(Role.ROLE_USER)
                .memberStatus(MemberStatus.ACTIVE)
                .build();

        product = Product.builder()
                .name("Test Product")
                .content("Test Content")
                .price(1000)
                .imgUrl("test.jpg")
                .quantity(10)
                .build();

        cartForm = new CartForm(1L, 1L, 5);

        cart = Cart.builder()
                .id(1L)
                .member(member)
                .product(product)
                .quantity(5)
                .build();
    }

    @Test
    @DisplayName("장바구니에 상품 추가 성공")
    void addCartItem_Success() {
        // given
        given(productService.findById(cartForm.productId())).willReturn(product);
        given(cartRepository.existsByProductId_IdAndMemberId_Id(cartForm.productId(), member.getId()))
                .willReturn(false);
        given(cartConverter.toCart(cartForm, member, product)).willReturn(cart);
        given(cartRepository.save(cart)).willReturn(cart);  // 구체적인 cart 객체 지정

        // when
        Long savedCartId = cartService.addCartItem(cartForm, member);

        // then
        assertThat(savedCartId).isEqualTo(cart.getId());
        verify(cartRepository).save(cart);  // 구체적인 cart 객체로 검증
    }

    @Test
    @DisplayName("회원 정보가 일치하지 않으면 예외 발생")
    void addCartItem_WithInvalidMember_ThrowsAuthException() {
        // given
        Member differentMember = Member.builder()
                .id(2L)
                .build();

        // when & then
        assertThatThrownBy(() -> cartService.addCartItem(cartForm, differentMember))
                .isInstanceOf(AuthException.class)
                .hasMessage("해당 유저가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("수량이 0 이하면 예외 발생")
    void addCartItem_WithInvalidQuantity_ThrowsCartException() {
        // given
        CartForm invalidCartForm = new CartForm(1L, 1L, 0);

        // when & then
        assertThatThrownBy(() -> cartService.addCartItem(invalidCartForm, member))
                .isInstanceOf(CartException.class)
                .hasMessage("상품을 최소 1개 이상 추가하여야 합니다.");
    }

    @Test
    @DisplayName("이미 장바구니에 존재하는 상품이면 예외 발생")
    void addCartItem_WithExistingProduct_ThrowsCartException() {
        // given
        given(cartRepository.existsByProductId_IdAndMemberId_Id(cartForm.productId(), member.getId()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> cartService.addCartItem(cartForm, member))
                .isInstanceOf(CartException.class)
                .hasMessage("이미 장바구니에 추가된 상품입니다.");
    }
}