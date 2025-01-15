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


    @Builder(builderMethodName = "create")
    public ProductOrders(Long id, Orders orders, Product product, int quantity, int price) {
        this.id = id;
        this.orders = orders;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }


}
