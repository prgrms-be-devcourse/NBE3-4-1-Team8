package com.example.backend.domain.cart.service;

import com.example.backend.domain.cart.dto.CartForm;
import com.example.backend.domain.cart.entity.Cart;
import com.example.backend.domain.cart.repository.CartRepository;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.repository.MemberRepository;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    public Long addCartItem(CartForm cartDto) {
        // 회원 조회
        Member member = memberRepository.findById(cartDto.getMemberId())
                .orElseThrow(()->new IllegalArgumentException("유효하지 않은 멤버ID"));

        // 상품 조회
        Product product = productRepository.findById(cartDto.getProductId())
                .orElseThrow(()->new IllegalArgumentException("유효하지 않은 상품ID"));

        // 재고 확인
        if(product.getQuantity() < cartDto.getQuantity()) {
            throw new IllegalStateException("재고가 부족합니다.");
        }

        // 장바구니 저장
        Cart cart = Cart.builder()
                .memberId(member)
                .productId(product)
                .quantity(cartDto.getQuantity())
                .build();

        // 상품 재고 감소
        product.decreaseQuantity(cartDto.getQuantity());
        productRepository.save(product);

        // 저장 후 ID 반환
        return cartRepository.save(cart).getId();

    }
}
