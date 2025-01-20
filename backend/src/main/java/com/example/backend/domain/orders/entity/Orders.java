package com.example.backend.domain.orders.entity;


import com.example.backend.domain.common.Address;
import com.example.backend.domain.member.entity.Member;
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

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
    private List<ProductOrders> productOrdersList = new ArrayList<>();

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private DeliveryStatus deliveryStatus;

    @Embedded
    private Address address;


    @Builder(builderMethodName = "create")
    public Orders(Member member, List<ProductOrders> productOrdersList, Address address) {
        this.member = member;
        calculateTotalPrice(productOrdersList);
        addProductOrder(productOrdersList);
        this.deliveryStatus = DeliveryStatus.READY;
        this.address = address;
    }

    /**
     * 연관관계 편의 메서드
     */
    public void addProductOrder(List<ProductOrders> productOrdersList){
        for (ProductOrders productOrders : productOrdersList) {
            this.productOrdersList.add(productOrders);
            productOrders.addOrders(this);
        }
    }

    /**
     * 주문가격 총합 조회
     */

    private void calculateTotalPrice(List<ProductOrders> productOrdersList){
        totalPrice = productOrdersList.stream()
                .mapToInt(ProductOrders::getTotalPrice)
                .sum();
    }

    public void changeStatus(DeliveryStatus status){
        this.deliveryStatus = status;
    }
}
