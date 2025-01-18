package com.example.backend.domain.product.controller;

import com.example.backend.domain.product.converter.ProductConverter;
import com.example.backend.domain.product.dto.ProductForm;
import com.example.backend.domain.product.dto.ProductResponse;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.product.exception.ProductErrorCode;
import com.example.backend.domain.product.exception.ProductException;
import com.example.backend.domain.product.service.ProductService;
import com.example.backend.global.config.CorsConfig;
import com.example.backend.global.config.TestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ProductControllerTest
 * ProductController 테스트 클래스
 *
 * @author 100minha
 */

@WebMvcTest(ProductController.class)
@Import({TestSecurityConfig.class, CorsConfig.class})
@WithMockUser(roles = "ADMIN")
public class ProductControllerTest {

    @MockitoBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("상품 등록 성공 테스트")
    void createSuccessTest() throws Exception {
        // given
        doNothing().when(productService).create(any(ProductForm.class));

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/products")
                        .content("""
                                {
                                    "name": "Test Product Name",
                                    "content": "Test Product content",
                                    "price": 1000,
                                    "imgUrl": "Test Product Image URL",
                                    "quantity": 10
                                }
                                """)
                .contentType(MediaType.APPLICATION_JSON)
                );

        // then
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("상품이 정상적으로 등록되었습니다."));
    }

    @Test
    @DisplayName("상품 이름 글자수 초과로 인한 등록 실패 테스트")
    void createFailWhenNameInvalidTest() throws Exception {
        // given
        doNothing().when(productService).create(any(ProductForm.class));

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/products")
                .content("""
                                {
                                    "name": "Test Product Name Test Product Name Test Product Name Test Product Name Test Product Name Test Product Name Test Product Name",
                                    "content": "Test Product content",
                                    "price": 1000,
                                    "imgUrl": "Test Product Image URL",
                                    "quantity": 10
                                }
                                """)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400-1"))
                .andExpect(jsonPath("$.errorDetails[0].field").value("name"))
                .andExpect(jsonPath("$.errorDetails[0].reason").value("상품 이름은 2자 이상 50자 이하여야 합니다."));
    }

    @Test
    @DisplayName("상품 가격 최솟값 미달로 인한 등록 실패 테스트")
    void createFailWhenPriceIsBelowMinTest() throws Exception {
        // given
        doNothing().when(productService).create(any(ProductForm.class));

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/products")
                .content("""
                                {
                                    "name": "Test Product Name",
                                    "content": "Test Product content",
                                    "price": 10,
                                    "imgUrl": "Test Product Image URL",
                                    "quantity": 10
                                }
                                """)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400-1"))
                .andExpect(jsonPath("$.errorDetails[0].field").value("price"))
                .andExpect(jsonPath("$.errorDetails[0].reason").value("상품 가격은 100원 이상이어야 합니다."));
    }

    @Test
    @DisplayName("상품 가격 최댓값 초과로 인한 등록 실패 테스트")
    void createFailWhenPriceIsAboveMaxTest() throws Exception {
        // given
        doNothing().when(productService).create(any(ProductForm.class));

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/products")
                .content("""
                                {
                                    "name": "Test Product Name",
                                    "content": "Test Product content",
                                    "price": 999999999,
                                    "imgUrl": "Test Product Image URL",
                                    "quantity": 10
                                }
                                """)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400-1"))
                .andExpect(jsonPath("$.errorDetails[0].field").value("price"))
                .andExpect(jsonPath("$.errorDetails[0].reason").value("상품 가격은 9,999,999원 이하여야 합니다."));
    }

    @Test
    @DisplayName("상품 가격 Integer 최댓값 초과로 인한 등록 실패 테스트")
    void createFailWhenPriceIsAboveMaxIntegerTest() throws Exception {
        // given
        doNothing().when(productService).create(any(ProductForm.class));

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/products")
                .content("""
                                {
                                    "name": "Test Product Name",
                                    "content": "Test Product content",
                                    "price": 99999999999,
                                    "imgUrl": "Test Product Image URL",
                                    "quantity": 10
                                }
                                """)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"));
    }

    @Test
    @DisplayName("상품 재고 음수 설정으로 인한 등록 실패 테스트")
    void createFailWhenQuantityIsBelowZeroTest() throws Exception {
        // given
        doNothing().when(productService).create(any(ProductForm.class));

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/products")
                .content("""
                                {
                                    "name": "Test Product Name",
                                    "content": "Test Product content",
                                    "price": 1000,
                                    "imgUrl": "Test Product Image URL",
                                    "quantity": -1
                                }
                                """)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400-1"))
                .andExpect(jsonPath("$.errorDetails[0].field").value("quantity"))
                .andExpect(jsonPath("$.errorDetails[0].reason").value("상품 수량은 0 이상이어야 합니다."));
    }

    @Test
    @DisplayName("상품 조회 성공 테스트")
    @WithAnonymousUser
    void findSuccessTest() throws Exception {
        //given
        Long id = 1L;
        Product product = Product.builder()
                .name("Test Product Name")
                .build();
        ProductResponse productResponse = ProductConverter.from(product);
        when(productService.findProductResponseById(id)).thenReturn(productResponse);

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/products/1" )
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("findById"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Test Product Name"));
    }

    @Test
    @DisplayName("상품 조회 실패 테스트")
    @WithAnonymousUser
    void findFailTest() throws Exception {
        //given
        doThrow(new ProductException(ProductErrorCode.NOT_FOUND)).when(productService).findProductResponseById(anyLong());

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/products/1" )
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("findById"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("상품을 찾을 수 없습니다."));
    }


}
