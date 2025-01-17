package com.example.backend.domain.cart.controller;

import com.example.backend.domain.cart.dto.CartForm;
import com.example.backend.domain.cart.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
    }

    @Test
    void addCartItem() throws Exception {
        // Given
        CartForm cartForm = new CartForm(1L, 1L, 1); // 테스트 데이터
        when(cartService.addCartItem(any(CartForm.class))).thenReturn(1L); // 서비스 메서드 Mocking

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/v1/cart")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(cartForm)))
                .andExpect(status().is(HttpStatus.OK.value())) // 상태 코드 확인
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }
}
