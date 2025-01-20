package com.example.backend.domain.orders.controller;

import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.entity.Role;
import com.example.backend.domain.orders.dto.OrdersForm;
import com.example.backend.domain.orders.dto.OrdersResponse;
import com.example.backend.domain.orders.dto.ProductInfoDto;
import com.example.backend.domain.orders.exception.OrdersErrorCode;
import com.example.backend.domain.orders.exception.OrdersException;
import com.example.backend.domain.orders.service.OrdersService;
import com.example.backend.domain.orders.status.DeliveryStatus;
import com.example.backend.global.auth.model.CustomUserDetails;
import com.example.backend.global.config.CorsConfig;
import com.example.backend.global.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdersController.class)
@Import({TestSecurityConfig.class, CorsConfig.class})
@WithMockUser()
public class OrdersControllerTest {

    @MockitoBean
    private OrdersService ordersService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("주문 단건 조회 성공")
    void findOneSuccessTest() throws Exception {
        // given
        Long orderId = 1L;
        OrdersResponse response = OrdersResponse.builder()
                .id(orderId)
                .products(List.of(
                        ProductInfoDto.builder()
                                .name("상품A")
                                .price(1000)
                                .quantity(2)
                                .imgUrl("http://example.com/productA.jpg")
                                .build()
                ))
                .totalPrice(2000)
                .status(DeliveryStatus.READY)
                .createAt(ZonedDateTime.now())
                .modifiedAt(ZonedDateTime.now())
                .build();

        when(ordersService.findOne(orderId)).thenReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/orders/{id}", orderId)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(handler().handlerType(OrdersController.class))
                .andExpect(handler().methodName("findOne"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(orderId))
                .andExpect(jsonPath("$.data.totalPrice").value(2000))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.products[0].name").value("상품A"))
                .andExpect(jsonPath("$.data.products[0].price").value(1000))
                .andExpect(jsonPath("$.data.products[0].quantity").value(2))
                .andExpect(jsonPath("$.data.products[0].imgUrl").value("http://example.com/productA.jpg"))
                .andExpect(jsonPath("$.data.createAt").exists())
                .andExpect(jsonPath("$.data.modifiedAt").exists());
    }

    @Test
    @DisplayName("주문 단건 조회 실패 - 존재하지 않는 주문")
    void findOneFailNotFoundTest() throws Exception {
        // given
        Long orderId = 999L;
        when(ordersService.findOne(orderId)).thenThrow(new OrdersException(OrdersErrorCode.NOT_FOUND));

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/orders/{id}", orderId)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(handler().handlerType(OrdersController.class))
                .andExpect(status().isNotFound())
                .andExpect(handler().methodName("findOne"))
                .andExpect(jsonPath("$.code").value(OrdersErrorCode.NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(OrdersErrorCode.NOT_FOUND.getMessage()));

    }

    @Test
    @DisplayName("현재 진행 중인 주문 조회 성공")
    void currentOrdersSuccessTest() throws Exception {
        // given
        Long memberId = 1L;
        List<OrdersResponse> mockOrderResponses = Arrays.asList(
                OrdersResponse.builder()
                        .id(1L)
                        .products(List.of(
                                ProductInfoDto.builder()
                                        .name("상품A")
                                        .price(1000)
                                        .quantity(2)
                                        .imgUrl("http://example.com/productA.jpg")
                                        .build()
                        ))
                        .totalPrice(2000)
                        .status(DeliveryStatus.READY)
                        .createAt(ZonedDateTime.now())
                        .modifiedAt(ZonedDateTime.now())
                        .build(),
                OrdersResponse.builder()
                        .id(2L)
                        .products(List.of(
                                ProductInfoDto.builder()
                                        .name("상품B")
                                        .price(3000)
                                        .quantity(1)
                                        .imgUrl("http://example.com/productB.jpg")
                                        .build()
                        ))
                        .totalPrice(3000)
                        .status(DeliveryStatus.SHIPPED)
                        .createAt(ZonedDateTime.now())
                        .modifiedAt(ZonedDateTime.now())
                        .build()
        );

        // READY 상태인 주문만 필터링
        List<OrdersResponse> readyOrders = mockOrderResponses.stream()
                .filter(order -> order.status() == DeliveryStatus.READY)
                .collect(Collectors.toList());

        // 인증된 사용자의 ID로 주문 목록 모킹
        when(ordersService.current(memberId)).thenReturn(readyOrders);

        // when, then
        mockMvc.perform(get("/api/v1/orders/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(createMockCustomUserDetails(memberId))))
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(OrdersController.class))
                .andExpect(handler().methodName("current"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].totalPrice").value(2000))
                .andExpect(jsonPath("$.data[0].status").value("READY"));

    }

    @Test
    @DisplayName("주문 히스토리 조회 성공")
    void historySuccess() throws Exception {
        // given
        Long memberId = 1L;
        List<OrdersResponse> mockOrderResponses = Arrays.asList(
                OrdersResponse.builder()
                        .id(1L)
                        .products(List.of(
                                ProductInfoDto.builder()
                                        .name("상품A")
                                        .price(1000)
                                        .quantity(2)
                                        .imgUrl("http://example.com/productA.jpg")
                                        .build()
                        ))
                        .totalPrice(2000)
                        .status(DeliveryStatus.READY)
                        .createAt(ZonedDateTime.now())
                        .modifiedAt(ZonedDateTime.now())
                        .build(),
                OrdersResponse.builder()
                        .id(2L)
                        .products(List.of(
                                ProductInfoDto.builder()
                                        .name("상품B")
                                        .price(3000)
                                        .quantity(1)
                                        .imgUrl("http://example.com/productB.jpg")
                                        .build()
                        ))
                        .totalPrice(3000)
                        .status(DeliveryStatus.SHIPPED)
                        .createAt(ZonedDateTime.now().plusHours(1))
                        .modifiedAt(ZonedDateTime.now().plusHours(1))
                        .build()
        );

        when(ordersService.history(1L)).thenReturn(mockOrderResponses);

        // when, then
        mockMvc.perform(get("/api/v1/orders/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(createMockCustomUserDetails(memberId))))
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(OrdersController.class))
                .andExpect(handler().methodName("history"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].totalPrice").value(2000))
                .andExpect(jsonPath("$.data[0].status").value("READY"))
                .andExpect(jsonPath("$.data[1].id").value(2L))
                .andExpect(jsonPath("$.data[1].totalPrice").value(3000))
                .andExpect(jsonPath("$.data[1].status").value("SHIPPED"));

    }

    @Test
    @DisplayName("주문 생성 성공")
    void createOrderSuccessTest() throws Exception {
        // given
        Long memberId = 1L;
        OrdersForm ordersForm = new OrdersForm(
                memberId,
                "서울",
                "강남구",
                "대한민국",
                "테헤란로 123",
                List.of(
                        new OrdersForm.ProductOrdersRequest(1L, 2),
                        new OrdersForm.ProductOrdersRequest(2L, 1)
                )
        );

        Long expectedOrderId = 100L;
        when(ordersService.create(any(OrdersForm.class), any(Member.class)))
                .thenReturn(expectedOrderId);

        // when, then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ordersForm))
                        .with(user(createMockCustomUserDetails(memberId))))
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(OrdersController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(jsonPath("$.data").value(expectedOrderId));
    }

    @Test
    @DisplayName("주문 생성 실패 - 상품 리스트 비어있음")
    void createOrderEmptyProductListFailTest() throws Exception {
        // given
        Long memberId = 1L;
        OrdersForm ordersForm = new OrdersForm(
                memberId,
                "서울",
                "강남구",
                "대한민국",
                "테헤란로 123",
                Collections.emptyList()
        );

        // when, then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ordersForm))
                        .with(user(createMockCustomUserDetails(memberId))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorDetails[0].field").value("productOrdersRequestList"))
                .andExpect(jsonPath("$.errorDetails[0].reason").value("상품 주문 리스트는 비어 있을 수 없습니다."));
    }

    @Test
    @DisplayName("주문 생성 실패 - 상품 수량 부적절")
    void createOrderInvalidQuantityFailTest() throws Exception {
        // given
        Long memberId = 1L;
        OrdersForm ordersForm = new OrdersForm(
                memberId,
                "서울",
                "강남구",
                "대한민국",
                "테헤란로 123",
                List.of(
                        new OrdersForm.ProductOrdersRequest(1L, 0)
                )
        );

        // when, then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ordersForm))
                        .with(user(createMockCustomUserDetails(memberId))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorDetails[0].field").value("productOrdersRequestList[0].quantity"))
                .andExpect(jsonPath("$.errorDetails[0].reason").value("수량은 1 이상이어야 합니다."));
    }

    @Test
    @DisplayName("주문 생성 실패 - 주소 정보 누락")
    void createOrderMissingAddressFailTest() throws Exception {
        // given
        Long memberId = 1L;
        OrdersForm ordersForm = new OrdersForm(
                memberId,
                "",
                "강남구",
                "대한민국",
                "테헤란로 123",
                List.of(
                        new OrdersForm.ProductOrdersRequest(1L, 2)
                )
        );

        // when, then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ordersForm))
                        .with(user(createMockCustomUserDetails(memberId))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorDetails[0].field").value("city"))
                .andExpect(jsonPath("$.errorDetails[0].reason").value("도시는 필수입니다."));
    }

    @Test
    @DisplayName("주문 정상 취소")
    void order_cancel_success() throws Exception {

        Long orderId = 1L;

        doNothing().when(ordersService).cancelById(orderId);

        mockMvc.perform(patch("/api/v1/orders/{id}", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(createMockCustomUserDetails(orderId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.success").value(true));

        verify(ordersService).cancelById(orderId);
    }


    // 테스트에서 사용할 MockCustomUserDetails 생성 메서드
    private CustomUserDetails createMockCustomUserDetails(Long memberId) {
        Member mockMember = Member.builder()
                .id(memberId)
                .role(Role.ROLE_USER)
                .build();
        return new CustomUserDetails(mockMember);

    }




}