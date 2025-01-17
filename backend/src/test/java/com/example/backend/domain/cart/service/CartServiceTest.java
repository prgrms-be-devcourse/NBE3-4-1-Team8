package com.example.backend.domain.cart.service;

import com.example.backend.domain.cart.dto.CartForm;
import com.example.backend.domain.cart.entity.Cart;
import com.example.backend.domain.cart.exception.CartException;
import com.example.backend.domain.cart.repository.CartRepository;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    CartRepository cartRepository;
    @Mock
    ProductService productService;
    @InjectMocks
    CartService cartService;

    private Member mockMember(Long id) {
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(id);
        return member;
    }

    private CartForm mockCartForm(Long productId, int quantity) {
        CartForm cartForm = mock(CartForm.class);
        when(cartForm.getProductId()).thenReturn(productId);
        when(cartForm.getQuantity()).thenReturn(quantity);
        return cartForm;
    }

    private Product mockProduct(Long productId) {
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(productId);
        return product;
    }

    @Test
    @DisplayName("장바구니에 상품 추가 성공")
    void addCartItem_success() {
        // Given
        Long memberId = 1L;
        Long productId = 1L;
        int quantity = 2;

        Member member = mockMember(memberId);
        CartForm cartForm = mockCartForm(productId, quantity);
        Product product = mockProduct(productId);

        when(cartRepository.existsByProductId_IdAndMemberId_Id(productId, memberId)).thenReturn(false);
        when(productService.findById(productId)).thenReturn(product);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Long cartId = cartService.addCartItem(cartForm, member);

        // Then
        assertThat(cartId).isNotNull();
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("장바구니에 이미 존재하는 상품 추가 시 예외 발생")
    void addCartItem_alreadyExists() {
        // Given
        Long memberId = 1L;
        Long productId = 1L;
        int quantity = 2;

        Member member = mockMember(memberId);
        CartForm cartForm = mockCartForm(productId, quantity);

        when(cartRepository.existsByProductId_IdAndMemberId_Id(productId, memberId)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> cartService.addCartItem(cartForm, member))
                .isInstanceOf(CartException.class)
                .hasMessage("이미 장바구니에 존재하는 상품입니다.");
    }

    @Test
    @DisplayName("장바구니에 유효하지 않은 수량으로 상품 추가 시 예외 발생")
    void addCartItem_invalidQuantity() {
        // Given
        Long memberId = 1L;
        Long productId = 1L;
        int quantity = 0;

        Member member = mockMember(memberId);
        CartForm cartForm = mockCartForm(productId, quantity);

        // When & Then
        assertThatThrownBy(() -> cartService.addCartItem(cartForm, member))
                .isInstanceOf(CartException.class)
                .hasMessage("수량은 0보다 커야 합니다.");
    }
}
