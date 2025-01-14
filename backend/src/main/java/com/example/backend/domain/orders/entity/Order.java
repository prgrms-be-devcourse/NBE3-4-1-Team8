package com.example.backend.domain.orders.entity;


import com.example.backend.domain.orders.status.DeliveryStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "orders")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "member_id")
    private Long memberId;
    @Column(name = "product_orders_id")
    private Long productOrdersId;
    @Column(name = "total_price")
    private int totalPrice;
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private DeliveryStatus deliveryStatus;

}
