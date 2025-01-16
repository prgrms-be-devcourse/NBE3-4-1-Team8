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
import com.example.backend.global.auth.model.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long addCartItem(CartForm cartForm) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long memberId = ((CustomUserDetails) authentication.getPrincipal()).getMember().getId();
        Long productId = cartForm.getProductId();
        int quantity = cartForm.getQuantity();

        // 유효성 검사
        if (quantity <= 0) {
            throw new CartException(CartErrorCode.INVALID_QUANTITY);
        }

        // 장바구니에 상품 존재 여부 확인(존재하면 exception 발생)
        if (cartRepository.existsByProductId_IdAndMemberId_Id(productId, memberId)) {
            throw new CartException(CartErrorCode.ALREADY_EXISTS_IN_CART);
        }

        // 회원 및 상품 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CartException(CartErrorCode.INVALID_MEMBER_ID));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CartException(CartErrorCode.INVALID_PRODUCT_ID));

        // 장바구니에 새로운 상품 추가
        Cart cart = Cart.builder()
                .memberId(member)
                .productId(product)
                .quantity(quantity)
                .build();

        return cartRepository.save(cart).getId();
    }
}