package com.example.backend.domain.cart.converter;

import com.example.backend.domain.cart.dto.CartForm;
import com.example.backend.domain.cart.dto.CartResponse;
import com.example.backend.domain.cart.entity.Cart;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.product.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class CartConverter {

    /**
     * CartForm -> Cart 엔티티 변환
     *
     * @param cartForm 요청 DTO
     * @param member   회원 엔티티
     * @param product  상품 엔티티
     * @return Cart 엔티티
     */
    public Cart toCart(CartForm cartForm, Member member, Product product) {
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
    public CartResponse toCartResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .memberId(cart.getMember().getId())
                .memberNickname(cart.getMember().getNickname())
                .productId(cart.getProduct().getId())
                .productName(cart.getProduct().getName())
                .quantity(cart.getQuantity())
                .productPrice(cart.getProduct().getPrice())
                .productImgUrl(cart.getProduct().getImgUrl())
                .build();
    }
}
