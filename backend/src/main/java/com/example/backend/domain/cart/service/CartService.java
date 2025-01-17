package com.example.backend.domain.cart.service;

import com.example.backend.domain.cart.dto.CartForm;
import com.example.backend.domain.cart.entity.Cart;
import com.example.backend.domain.cart.exception.CartErrorCode;
import com.example.backend.domain.cart.exception.CartException;
import com.example.backend.domain.cart.repository.CartRepository;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.product.service.ProductService;
import com.example.backend.global.auth.exception.AuthErrorCode;
import com.example.backend.global.auth.exception.AuthException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
//Long memberId = customUserDetails.getMember().getId();
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;

    @Transactional
    public Long addCartItem(CartForm cartForm, Member member) {

        if (member.getId() != cartForm.getMemberId()) {
            throw new AuthException(AuthErrorCode.MEMBER_NOT_FOUND);
        }

        Long productId = cartForm.getProductId();
        int quantity = cartForm.getQuantity();

        // 유효성 검사
        if (quantity <= 0) {
            throw new CartException(CartErrorCode.INVALID_QUANTITY);
        }

        // 장바구니에 상품 존재 여부 확인(존재하면 exception 발생)
        if (cartRepository.existsByProductId_IdAndMemberId_Id(productId, member.getId())) {
            throw new CartException(CartErrorCode.ALREADY_EXISTS_IN_CART);
        }

        // 장바구니에 새로운 상품 추가
        Cart cart = Cart.builder()
                .member(member)
                .product(productService.findById(productId))
                .quantity(quantity)
                .build();

        return cartRepository.save(cart).getId();
    }
}
