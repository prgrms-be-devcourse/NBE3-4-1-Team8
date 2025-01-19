package com.example.backend.domain.orders.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import com.example.backend.domain.common.Address;
import com.example.backend.domain.product.exception.ProductErrorCode;
import com.example.backend.domain.product.exception.ProductException;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@Slf4j
class OrdersServiceTest {

    @Mock
    OrdersRepository ordersRepository;
    @InjectMocks
    OrdersService ordersService;

    private Orders mockOrder(Long id, DeliveryStatus status) {
        Orders orders = mock(Orders.class);
        ZonedDateTime now = ZonedDateTime.now();
        List<ProductOrders> productOrders = mockProductOrders();

        when(orders.getId()).thenReturn(id);
        when(orders.getTotalPrice()).thenReturn(1000);
        when(orders.getDeliveryStatus()).thenReturn(status);
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
        lenient().when(product.getImgUrl()).thenReturn("http://example.com/productA.jpg");
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
        assertThat(ordersResponse.id()).isEqualTo(orderId);
        assertThat(ordersResponse.totalPrice()).isEqualTo(orders.getTotalPrice());
        assertThat(ordersResponse.status()).isEqualTo(DeliveryStatus.READY);  // READY를 기대
        assertThat(ordersResponse.createAt()).isEqualTo(orders.getCreatedAt());
        assertThat(ordersResponse.modifiedAt()).isEqualTo(orders.getModifiedAt());

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
        // + converter 에 정렬 추가되면서 해당 부분도 수정시간순으로 정렬
        OrdersResponse firstOrder = result.get(0);
        assertThat(firstOrder.id()).isEqualTo(2L);
        assertThat(firstOrder.totalPrice()).isEqualTo(1000);
        assertThat(firstOrder.products()).hasSize(1);

        // 메서드 호출 검증
        verify(ordersRepository).findByMemberIdAndDeliveryStatus(
                member.getId(),
                DeliveryStatus.READY
        );
    }

    @Test
    @DisplayName("주문 총 가격 계산 성공")
    void calculateTotalPrice() {
        // Given
        Product product1 = mock(Product.class);
        lenient().when(product1.getPrice()).thenReturn(1000);

        Product product2 = mock(Product.class);
        lenient().when(product2.getPrice()).thenReturn(2000);

        ProductOrders productOrder1 = mock(ProductOrders.class);
        lenient().when(productOrder1.getProduct()).thenReturn(product1);
        lenient().when(productOrder1.getQuantity()).thenReturn(2);
        lenient().when(productOrder1.getTotalPrice()).thenReturn(1000 * 2);

        ProductOrders productOrder2 = mock(ProductOrders.class);
        lenient().when(productOrder2.getProduct()).thenReturn(product2);
        lenient().when(productOrder2.getQuantity()).thenReturn(3);
        lenient().when(productOrder2.getTotalPrice()).thenReturn(2000 * 3);

        Member member = mock(Member.class);
        Address address = mock(Address.class);

        // When
        Orders orders = Orders.create()
                .member(member)
                .productOrdersList(List.of(productOrder1, productOrder2))
                .address(address)
                .build();

        // Then
        assertThat(orders.getTotalPrice()).isEqualTo(2000 + 6000);
    }

    @Test
    @DisplayName("주문 시 수량 만큼 재고 감소")
    void reduceQuantity() {
        // Given
        Product product1 = mock(Product.class);
        lenient().when(product1.getPrice()).thenReturn(1000);
        lenient().when(product1.getQuantity()).thenReturn(100);

        ProductOrders productOrders1 = ProductOrders.create()
                .product(product1)
                .quantity(10)
                .price(product1.getPrice())
                .build();

        Member member = mock(Member.class);
        Address address = mock(Address.class);

        // When
        Orders.create()
                .member(member)
                .productOrdersList(List.of(productOrders1))
                .address(address)
                .build();

        // Then
        verify(product1).removeQuantity(10);
    }

    @Test
    @DisplayName("재고 부족 시 에러 발생")
    void notEnoughQuantity() {

        // Given
        Product product1 = mock(Product.class);
        lenient().when(product1.getPrice()).thenReturn(1000);
        lenient().when(product1.getQuantity()).thenReturn(100);

        doThrow(new ProductException(ProductErrorCode.INSUFFICIENT_QUANTITY))
                .when(product1).removeQuantity(101);

        Member member = mock(Member.class);
        Address address = mock(Address.class);

        // When & Then
        assertThatThrownBy(() -> {
            ProductOrders productOrders1 = ProductOrders.create()
                    .product(product1)
                    .quantity(101)
                    .price(product1.getPrice())
                    .build();

            Orders order = Orders.create()
                    .member(member)
                    .address(address)
                    .productOrdersList(List.of(productOrders1))
                    .build();
        }).isInstanceOf(ProductException.class)
                .hasMessage(ProductErrorCode.INSUFFICIENT_QUANTITY.getMessage());
    }
    @Test
    @DisplayName("모든 주문 목록 조회 성공")
    void history() {
        // Given
        String username = "testUser";
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(1L);

        // orders 목록 mock
        Orders o1 = mockOrder(1L, DeliveryStatus.READY);
        Orders o2 = mockOrder(2L, DeliveryStatus.SHIPPED);
        List<Orders> ordersList = List.of(
                o1,
                o2
        );

        log.info("o1={}",o1.getModifiedAt());
        log.info("o2={}",o2.getModifiedAt());

        when(ordersRepository.findAllByMemberIdAndDeliveryStatusOrderByModifiedAt(
                member.getId(),
                List.of(
                        DeliveryStatus.READY,
                        DeliveryStatus.SHIPPED)
        )).thenReturn(ordersList);

        // When
        List<OrdersResponse> result = ordersService.history(member.getId());

        // Then
        assertThat(result).hasSize(2);

        OrdersResponse firstOrder = result.get(0);
        // 수정시간이 최신인 주문이 먼저 조회되어야함
        assertThat(firstOrder.id()).isEqualTo(o2.getId());
        assertThat(firstOrder.products()).hasSize(1);

        // 메서드 호출 검증
        verify(ordersRepository).findAllByMemberIdAndDeliveryStatusOrderByModifiedAt(
                member.getId(),
                List.of(
                        DeliveryStatus.READY,
                        DeliveryStatus.SHIPPED)
        );
    }
}
