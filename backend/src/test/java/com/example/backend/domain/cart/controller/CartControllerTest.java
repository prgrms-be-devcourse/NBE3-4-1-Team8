package com.example.backend.domain.cart.controller;

import com.example.backend.domain.cart.dto.CartForm;
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
}
