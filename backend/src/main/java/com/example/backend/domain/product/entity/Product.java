package com.example.backend.domain.product.entity;

import com.example.backend.domain.product.exception.ProductErrorCode;
import com.example.backend.domain.product.exception.ProductException;
import com.example.backend.domain.product.dto.ProductForm;
import com.example.backend.domain.product.exception.ProductErrorCode;
import com.example.backend.domain.product.exception.ProductException;

import com.example.backend.domain.product.dto.ProductForm;
import com.example.backend.domain.product.exception.ProductErrorCode;
import com.example.backend.domain.product.exception.ProductException;
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

    @Column(length = 50, unique = true)
    private String name;

    private String content;

    private int price;

    private String imgUrl;

    private int quantity;

    @Version
    private Long version;

    @Builder
    public Product(String name, String content, int price, String imgUrl, int quantity) {
        this.name = name;
        this.content = content;
        this.price = price;
        this.imgUrl = imgUrl;
        this.quantity = quantity;
    }

    /**
     * 상품 재고 감소 로직
     */
    public void removeQuantity(int quantity) {

        int restQuantity = this.quantity - quantity;
        if(restQuantity < 0) {
            throw new ProductException(ProductErrorCode.INSUFFICIENT_QUANTITY);
        }
        this.quantity = restQuantity;
    }
    public void modify(ProductForm productForm) {

        this.name = productForm.name();
        this.content = productForm.content();
        this.price = productForm.price();
        this.imgUrl = productForm.imgUrl();
        this.quantity = productForm.quantity();
    }

    public void restore(int quantity) {
        this.quantity += quantity;
    }

}
