package com.example.backend.domain.order.entity;


import com.example.backend.domain.order.status.DeliveryStatus;
import com.example.backend.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;

@Entity
@Table(name = "orders")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // 외래 키 매핑
    private Member member;

    @OneToMany(mappedBy = "order")
    @Builder.Default
    private List<ProductOrders> productOrders = new ArrayList<>();

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private DeliveryStatus deliveryStatus;

    // todo 연관관계 메서드 구현 필요


}
