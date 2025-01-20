package com.example.backend.domain.cart.service;

import com.example.backend.domain.cart.converter.CartConverter;
import com.example.backend.domain.cart.dto.CartDeleteForm;
import com.example.backend.domain.cart.dto.CartForm;
import com.example.backend.domain.cart.dto.CartResponse;
import com.example.backend.domain.cart.dto.CartUpdateForm;
import com.example.backend.domain.cart.entity.Cart;
import com.example.backend.domain.cart.exception.CartErrorCode;
import com.example.backend.domain.cart.exception.CartException;
import com.example.backend.domain.cart.repository.CartRepository;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;

    @Transactional
    public Long addCartItem(CartForm cartForm, Member member) {
        // 요청한 상품 ID로 상품 조회
        Long productId = cartForm.productId();

        // 이미 장바구니에 있는 상품인 경우 exception 발생
        if (cartRepository.existsByProductIdAndMemberId(productId, member.getId())) {
            throw new CartException(CartErrorCode.ALREADY_EXISTS_IN_CART);
        }

        // 장바구니에 상품 추가
        Cart cart = CartConverter.from(
                cartForm,
                member,
                productService.findById(productId)
        );

        return cartRepository.save(cart).getId();
    }

    @Transactional(readOnly = true)
    public List<CartResponse> getCartByMember(Member member) {
        List<Cart> cartList = cartRepository.findAllByMemberWithProducts(member);

        return CartConverter.toResponseList(cartList);
    }

    @Transactional
    public void deleteByMemberId(Long memberId) {
        cartRepository.deleteByMemberId(memberId);
    }

    @Transactional
    public Long updateCartItemQuantity(CartUpdateForm cartUpdateForm, Member member) {
        // 해당 상품이 장바구니에 있는지 조회 후 없으면 exception 발생
        Cart cart = cartRepository.findByProductIdAndMemberId(cartUpdateForm.productId(), member.getId())
                .orElseThrow(() -> new CartException(CartErrorCode.PRODUCT_NOT_FOUND_IN_CART));

        // 현재 수량과 동일한 수량인 경우 exception 발생
        if (cart.getQuantity() == cartUpdateForm.quantity()) {
            throw new CartException(CartErrorCode.SAME_QUANTITY_IN_CART);
        }

        // 수량 업데이트
        cart.updateQuantity(cartUpdateForm.quantity());

        return cart.getId();
    }

    @Transactional
    public Long deleteCartItem(CartDeleteForm cartDeleteForm, Member member) {
        // 해당 상품이 장바구니에 있는지 조회 후 없으면 exception 발생
        Cart cart = cartRepository.findByProductIdAndMemberId(cartDeleteForm.productId(), member.getId())
                .orElseThrow(() -> new CartException(CartErrorCode.PRODUCT_NOT_FOUND_IN_CART));

        cartRepository.delete(cart);

        return cart.getProduct().getId();
    }
}
