package com.example.backend.domain.orders.service;

import com.example.backend.domain.orders.dto.OrderResponse;
import com.example.backend.domain.orders.entity.Orders;
import com.example.backend.domain.orders.exception.OrdersErrorCode;
import com.example.backend.domain.orders.exception.OrdersException;
import com.example.backend.domain.orders.repository.OrderRepository;
import com.example.backend.domain.orders.repository.OrderRepositoryTest;

import com.example.backend.domain.orders.status.DeliveryStatus;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.product.repository.ProductRepository;

import com.example.backend.domain.productOrders.entity.ProductOrders;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(orderRepository);
    }

    private Orders mockOrder(Long id) {
        // Orders 객체를 mock
        Orders orders = mock(Orders.class);
        when(orders.getId()).thenReturn(id);
        when(orders.getTotalPrice()).thenReturn(1000);
        when(orders.getDeliveryStatus()).thenReturn(DeliveryStatus.READY);

        // ProductOrders mock
        List<ProductOrders> productOrders = mockProductOrders(orders);
        when(orders.getProductOrders()).thenReturn(productOrders);

        return orders;
    }

    private List<ProductOrders> mockProductOrders(Orders orders) {
        // ProductOrders 객체 mock
        ProductOrders productOrder = mock(ProductOrders.class);
        Product product = mock(Product.class);

        when(product.getId()).thenReturn(1L);
        when(product.getName()).thenReturn("A");
        when(product.getImgUrl()).thenReturn("http://example.com/productA.jpg");

        when(productOrder.getId()).thenReturn(1L);
        when(productOrder.getPrice()).thenReturn(5000);
        when(productOrder.getQuantity()).thenReturn(3);
        when(productOrder.getProduct()).thenReturn(product);

        // 양방향 관계 설정 반드시 해줘야 한다
        when(productOrder.getOrders()).thenReturn(orders);  // ProductOrders에서 Orders 참조

        return List.of(productOrder);
    }

    @Test
    @DisplayName("단건 조회 성공")
    void findOne() {
        // Given
        Long orderId = 1L;
        Orders orders = mockOrder(orderId);  // Mock or create an Orders object

        when(orderRepository.findOrderById(orderId)).thenReturn(Optional.of(orders));

        // When
        OrderResponse orderResponse = orderService.findOne(orderId);

        // Then

        assertThat(orderId).isEqualTo(orderResponse.getId());
        assertThat(orders.getTotalPrice()).isEqualTo(orderResponse.getTotalPrice());
        assertThat(orders.getDeliveryStatus()).isEqualTo(orderResponse.getStatus());
        assertThat(orders.getCreatedAt()).isEqualTo(orderResponse.getCreateAt());
        assertThat(orders.getModifiedAt()).isEqualTo(orderResponse.getModifiedAt());
        assertThat(orders.getProductOrders().size()).isEqualTo(1);
        assertThat(orders.getProductOrders().get(0).getProduct().getName()).isEqualTo("A");
        assertThat(orders.getProductOrders().get(0).getProduct().getImgUrl()).isEqualTo("http://example.com/productA.jpg");

    }

    @Test
    @DisplayName("단건 조회 NOT FOUND")
    void not_found() {
        // Given
        Long orderId = 1L;
        when(orderRepository.findOrderById(orderId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> orderService.findOne(orderId))
                .isInstanceOf(OrdersException.class)
                .hasMessage("해당 리소스를 찾을 수 없습니다");

    }

}
