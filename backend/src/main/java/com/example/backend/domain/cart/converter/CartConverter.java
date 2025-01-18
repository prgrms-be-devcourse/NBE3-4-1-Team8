package com.example.backend.domain.cart.converter;

import com.example.backend.domain.cart.dto.CartForm;
import com.example.backend.domain.cart.dto.CartResponse;
import com.example.backend.domain.cart.entity.Cart;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.product.entity.Product;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartConverter {

    /**
     * CartForm -> Cart 엔티티 변환
     *
     * @param cartForm 요청 DTO
     * @param member   회원 엔티티
     * @param product  상품 엔티티
     * @return Cart 엔티티
     */
    public static Cart from(CartForm cartForm, Member member, Product product) {
        return Cart.builder()
                .member(member)
                .product(product)
                .quantity(cartForm.quantity())
                .build();
    }

    /**
     * Cart 엔티티 -> CartResponse 변환
     *
     * @param cart Cart 엔티티
     * @return 응답 DTO
     */
    public static CartResponse toResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .productName(cart.getProduct().getName())
                .quantity(cart.getQuantity())
                .productPrice(cart.getProduct().getPrice())
                .totalPrice(cart.getProduct().getPrice() * cart.getQuantity())
                .productImgUrl(cart.getProduct().getImgUrl())
                .build();
    }
}
