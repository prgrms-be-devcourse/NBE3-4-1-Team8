package com.example.backend.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
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
