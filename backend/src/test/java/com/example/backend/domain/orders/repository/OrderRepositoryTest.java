package com.example.backend.domain.orders.repository;


import com.example.backend.domain.common.Address;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.entity.Role;
import com.example.backend.domain.member.repository.MemberRepository;
import com.example.backend.domain.orders.entity.Orders;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.product.repository.ProductRepository;
import com.example.backend.domain.productOrders.entity.ProductOrders;
import com.example.backend.domain.productOrders.repository.ProductOrdersRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Transactional
@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductOrdersRepository productOrdersRepository;


    @Test
    @DisplayName("주문 저장 성공")
    void saveOrder() {

        // Member 객체 생성
        Member member = Member.builder()
                .username("test")
                .nickname("test")
                .password("123")
                .role(Role.ROLE_USER)
                .address(new Address("123 Main St", "New York", "NY", "10001"))
                .createdAt(ZonedDateTime.now())
                .modifiedAt(ZonedDateTime.now())
                .build();

        Member savedMember = memberRepository.save(member);

        // Product 객체 생성
        Product product = Product.builder()
                .name("test")
                .content("test")
                .price(100)
                .imgUrl("test")
                .quantity(10)
                .build();

        Product savedProduct = productRepository.save(product);

        // ProductOrders 객체 생성
        ProductOrders productOrders = ProductOrders.create()
                .product(product)
                .quantity(2)
                .price(100)
                .build();
        ProductOrders savedProductOrders = productOrdersRepository.save(productOrders);

        // ProductOrders 목록 준비
        List<ProductOrders> productOrdersList = new ArrayList<>();
        productOrdersList.add(savedProductOrders);

        // Orders 객체 생성
        Orders orders = Orders.create()
                .member(savedMember)
                .productOrders(productOrdersList)
                .totalPrice(200)
                .build();

        // 주문 저장
        Orders savedOrder = orderRepository.save(orders);

        // 저장된 주문이 예상한 주문과 동일한지 검증
        Assertions.assertThat(orders).isEqualTo(savedOrder);
        Assertions.assertThat(orders.getMember()).isEqualTo(savedOrder.getMember());
        Assertions.assertThat(orders.getProductOrders()).isEqualTo(savedOrder.getProductOrders());
        Assertions.assertThat(200).isEqualTo(savedOrder.getTotalPrice());
    }
}
