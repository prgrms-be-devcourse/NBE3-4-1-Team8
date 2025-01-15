package com.example.backend.domain.product.entity;

import com.example.backend.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Product
 * 상품 Entity
 * @author 100minha
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String name;

    private String content;

    private int price;

    private String imgUrl;

    private int quantity;

    @Builder
    public Product(String name, String content, int price, String imgUrl, int quantity) {
        this.name = name;
        this.content = content;
        this.price = price;
        this.imgUrl = imgUrl;
        this.quantity = quantity;
    }
}