package com.example.backend.domain.cart.service;

import com.example.backend.domain.cart.dto.CartForm;
import com.example.backend.domain.cart.entity.Cart;
import com.example.backend.domain.cart.exception.CartErrorCode;
import com.example.backend.domain.cart.exception.CartException;
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

    public Long addCartItem(CartForm cartForm) {
        // 회원 조회
        Member member = memberRepository.findById(cartForm.getMemberId())
                .orElseThrow(()->new CartException(CartErrorCode.INVALID_MEMBER_ID));

        // 상품 조회
        Product product = productRepository.findById(cartForm.getProductId())
                .orElseThrow(()->new CartException(CartErrorCode.INVALID_PRODUCT_ID));

        // 현재 장바구니에 있는 동일 상품의 수량 합산
        int currentCartQuantity = cartRepository.findByMemberIdAndProductId(member, product)
                .map(Cart::getQuantity)
                .orElse(0);

        // 재고 확인
        if (cartForm.getQuantity() + currentCartQuantity > product.getQuantity()) {
            throw new CartException(CartErrorCode.INSUFFICIENT_STOCK);
        }

        // 요청된 상품 -> 장바구니 저장
        Cart cart = Cart.builder()
                .memberId(member)
                .productId(product)
                .quantity(cartForm.getQuantity())
                .build();

        // 저장 후 ID 반환
        return cartRepository.save(cart).getId();

    }
}