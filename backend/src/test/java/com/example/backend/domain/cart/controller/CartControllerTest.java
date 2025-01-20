package com.example.backend.domain.cart.controller;

import com.example.backend.domain.cart.dto.CartDeleteForm;
import com.example.backend.domain.cart.dto.CartForm;
import com.example.backend.domain.cart.dto.CartResponse;
import com.example.backend.domain.cart.dto.CartUpdateForm;
import com.example.backend.domain.cart.exception.CartErrorCode;
import com.example.backend.domain.cart.exception.CartException;
import com.example.backend.domain.cart.service.CartService;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.global.auth.model.CustomUserDetails;
import com.example.backend.global.response.GenericResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private CustomUserDetails customUserDetails;

    @InjectMocks
    private CartController cartController;

    private Member member;
    private CartForm cartForm;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Member 객체 모킹
        member = mock(Member.class);
        when(member.getId()).thenReturn(1L);
        when(customUserDetails.getMember()).thenReturn(member);

        cartForm = new CartForm(1L, 1L, 2);
    }

    /**
     * addCartItem() 메서드 테스트
     * - 장바구니에 상품 추가
     * @throws CartException
     */
    @Test
    @WithMockUser
    @DisplayName("장바구니에 상품 추가")
    void addCartItem() throws CartException {
        Long cartId = 1L;

        when(cartService.addCartItem(cartForm, member)).thenReturn(cartId);

        ResponseEntity<GenericResponse<Long>> response = cartController.addCartItem(cartForm, customUserDetails);

        // then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(cartId, response.getBody().getData());

        verify(cartService).addCartItem(cartForm, member);
    }

    /**
     * getCarts() 메서드 테스트
     * - 회원의 장바구니 목록 조회
     * - 빈 장바구니 목록 조회
     * @throws CartException
     */
    @Test
    @WithMockUser
    @DisplayName("회원의 장바구니 목록 조회 성공")
    void getCarts_Success() {
        // given
        Long memberId = 1L;
        CartResponse cartResponse = new CartResponse(1L,"testProductName", 10, 1000, 10000, "test.jpg");
        List<CartResponse> cartResponses = List.of(cartResponse);

        when(cartService.getCartByMember(member)).thenReturn(cartResponses);

        // when
        ResponseEntity<GenericResponse<List<CartResponse>>> response =
                cartController.getCart(customUserDetails);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getData().size());
        assertEquals(cartResponse.productName(), response.getBody().getData().get(0).productName());

        verify(cartService).getCartByMember(member);
    }

    @Test
    @WithMockUser
    @DisplayName("빈 장바구니 목록 조회")
    void getCarts_WithEmptyCart_ReturnsEmptyList() {
        // given
        Long memberId = 1L;
        List<CartResponse> emptyCartResponses = List.of();

        when(cartService.getCartByMember(member)).thenReturn(emptyCartResponses);

        // when
        ResponseEntity<GenericResponse<List<CartResponse>>> response =
                cartController.getCart(customUserDetails);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getData().isEmpty());

        verify(cartService).getCartByMember(member);
    }

    /**
     * updateCartItemQuantity() 메서드 테스트
     * - 장바구니 상품 수량 변경
     * @throws CartException
     */
    @Test
    @WithMockUser
    @DisplayName("장바구니 상품 수량 변경")
    void updateCartItemQuantity() throws CartException {
        // given
        Long cartId = 1L;
        CartUpdateForm cartUpdateForm = new CartUpdateForm(1L, 3);

        when(cartService.updateCartItemQuantity(cartUpdateForm, member)).thenReturn(cartId);

        // when
        ResponseEntity<GenericResponse<Long>> response = cartController
                .updateCartItemQuantity(cartUpdateForm, customUserDetails);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(cartId, response.getBody().getData());

        verify(cartService).updateCartItemQuantity(cartUpdateForm, member);
    }

    /**
     * deleteCartItem() 메서드 테스트
     * - 장바구니 상품 삭제 성공
     * - 존재하지 않는 상품 삭제 시도
     */
    @Test
    @WithMockUser
    @DisplayName("장바구니 상품 삭제 성공")
    void deleteCartItem_Success() {
        // given
        Long productId = 1L;
        CartDeleteForm cartDeleteForm = new CartDeleteForm(productId);

        when(cartService.deleteCartItem(cartDeleteForm, member)).thenReturn(productId);

        // when
        ResponseEntity<GenericResponse<Long>> response =
                cartController.deleteCartItem(cartDeleteForm, customUserDetails);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productId, response.getBody().getData());

        verify(cartService).deleteCartItem(cartDeleteForm, member);
    }

    @Test
    @WithMockUser
    @DisplayName("존재하지 않는 장바구니 상품 삭제 시도")
    void deleteCartItem_WithNonExistingProduct_ThrowsException() {
        // given
        Long nonExistingProductId = 999L;
        CartDeleteForm cartDeleteForm = new CartDeleteForm(nonExistingProductId);

        when(cartService.deleteCartItem(cartDeleteForm, member))
                .thenThrow(new CartException(CartErrorCode.PRODUCT_NOT_FOUND_IN_CART));

        // when & then
        CartException exception = assertThrows(CartException.class, () ->
                cartController.deleteCartItem(cartDeleteForm, customUserDetails));

        assertEquals(CartErrorCode.PRODUCT_NOT_FOUND_IN_CART.getCode(), exception.getCode());

        verify(cartService).deleteCartItem(cartDeleteForm, member);
    }
}
