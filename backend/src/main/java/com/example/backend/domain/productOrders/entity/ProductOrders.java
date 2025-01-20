package com.example.backend.domain.productOrders.entity;


import com.example.backend.domain.orders.entity.Orders;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOrders extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Orders orders;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    @Column(name = "quantity", nullable = false)
    private int quantity;
    @Column(name = "price", nullable = false)
    private int price;

    /**
     * 생성 메서드
     */
    @Builder(builderMethodName = "create")
    public ProductOrders(Product product, int quantity, int price) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;

        product.removeQuantity(quantity); // 주문 수량 만큼 감소
    }

    /**
     * 연관관계 편의 메서드
     */

    public void addOrders(Orders orders){
        this.orders = orders;
    }
    /**
     * 주문상품 가격 총합 조회
     */
    public int getTotalPrice() {
        return getPrice() * getQuantity();
    }

    /**
     * 상품 수량 복구
     */
    public void restore(int quantity) {
        product.restore(quantity);
    }

}
