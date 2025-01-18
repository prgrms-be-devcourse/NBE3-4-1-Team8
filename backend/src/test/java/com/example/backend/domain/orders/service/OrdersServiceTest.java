package com.example.backend.domain.orders.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.repository.MemberRepository;
import com.example.backend.domain.orders.dto.OrdersResponse;
import com.example.backend.domain.orders.entity.Orders;
import com.example.backend.domain.orders.exception.OrdersException;
import com.example.backend.domain.orders.repository.OrdersRepository;
import com.example.backend.domain.orders.status.DeliveryStatus;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.productOrders.entity.ProductOrders;

@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {

    @Mock
    OrdersRepository ordersRepository;
    @Mock
    MemberRepository memberRepository;
    @InjectMocks
    OrdersService ordersService;

    private Orders mockOrder(Long id, DeliveryStatus status) {
        Orders orders = mock(Orders.class);
        ZonedDateTime now = ZonedDateTime.now();
        List<ProductOrders> productOrders = mockProductOrders();

        when(orders.getId()).thenReturn(id);
        when(orders.getTotalPrice()).thenReturn(1000);
        lenient().when(orders.getDeliveryStatus()).thenReturn(status);
        when(orders.getCreatedAt()).thenReturn(now);
        when(orders.getModifiedAt()).thenReturn(now);
        when(orders.getProductOrdersList()).thenReturn(productOrders);

        return orders;
    }

    private List<ProductOrders> mockProductOrders() {
        ProductOrders productOrder = mock(ProductOrders.class);
        Product product = mock(Product.class);

        // OrdersResponse에서 실제로 사용하는 필드만 stub
        when(product.getName()).thenReturn("A");
        when(product.getImgUrl()).thenReturn("http://example.com/productA.jpg");
        when(productOrder.getProduct()).thenReturn(product);

        return List.of(productOrder);
    }

    @Test
    @DisplayName("단건 조회 성공")
    void findOne() {
        // Given
        Long orderId = 1L;
        Orders orders = mockOrder(orderId, DeliveryStatus.READY);
        when(ordersRepository.findOrderById(orderId)).thenReturn(Optional.of(orders));

        // When
        OrdersResponse ordersResponse = ordersService.findOne(orderId);

        // Then
        assertThat(orderId).isEqualTo(ordersResponse.getId());
        assertThat(orders.getTotalPrice()).isEqualTo(ordersResponse.getTotalPrice());
        assertThat(orders.getDeliveryStatus()).isEqualTo(ordersResponse.getStatus());
        assertThat(orders.getCreatedAt()).isEqualTo(ordersResponse.getCreateAt());
        assertThat(orders.getModifiedAt()).isEqualTo(ordersResponse.getModifiedAt());

        ProductOrders firstProductOrder = orders.getProductOrdersList().get(0);
        assertThat(firstProductOrder.getProduct().getName()).isEqualTo("A");
        assertThat(firstProductOrder.getProduct().getImgUrl()).isEqualTo("http://example.com/productA.jpg");

        verify(ordersRepository).findOrderById(orderId);
    }

    @Test
    @DisplayName("단건 조회 NOT FOUND")
    void not_found() {
        // Given
        Long orderId = 1L;
        when(ordersRepository.findOrderById(orderId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> ordersService.findOne(orderId))
                .isInstanceOf(OrdersException.class)
                .hasMessage("해당 리소스를 찾을 수 없습니다");

    }

    @Test
    @DisplayName("현재 진행중인 주문 목록 조회 성공")
    void current() {
        // Given
        String username = "testUser";
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(1L);

        // orders 목록 mock
        List<Orders> ordersList = List.of(
                mockOrder(1L,DeliveryStatus.READY),
                mockOrder(2L,DeliveryStatus.READY)
        );

        when(ordersRepository.findByMemberIdAndDeliveryStatus(
                member.getId(),
                DeliveryStatus.READY
        )).thenReturn(ordersList);

        // When
        List<OrdersResponse> result = ordersService.current(member.getId());

        // Then
        assertThat(result).hasSize(2);

        // 첫 번째 주문 검증
        OrdersResponse firstOrder = result.get(0);
        assertThat(firstOrder.getId()).isEqualTo(1L);
        assertThat(firstOrder.getTotalPrice()).isEqualTo(1000);
        assertThat(firstOrder.getProducts()).hasSize(1);

        // 메서드 호출 검증
        verify(ordersRepository).findByMemberIdAndDeliveryStatus(
                member.getId(),
                DeliveryStatus.READY
        );
    }
}
