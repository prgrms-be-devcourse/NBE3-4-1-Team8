package com.example.backend.domain.orders.entity;


import com.example.backend.domain.orders.status.DeliveryStatus;
import com.example.backend.domain.productOrders.entity.ProductOrders;
import com.example.backend.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Orders extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // 외래 키 매핑
    private Member member;

    @OneToMany(mappedBy = "order")
    private List<ProductOrders> productOrders = new ArrayList<>();

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private DeliveryStatus deliveryStatus;


    @Builder(builderMethodName = "create")
    public Orders(Long id, Member member, List<ProductOrders> productOrders, int totalPrice) {
        this.id = id;
        this.member
        this.productOrders = productOrders;
        this.totalPrice = totalPrice;
        this.deliveryStatus = DeliveryStatus.READY;
    }

    // todo 연관관계 메서드 구현 필요


}
