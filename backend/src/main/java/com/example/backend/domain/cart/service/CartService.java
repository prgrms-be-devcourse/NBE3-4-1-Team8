package com.example.backend.domain.cart.service;

import com.example.backend.domain.cart.converter.CartConverter;
import com.example.backend.domain.cart.dto.CartForm;
import com.example.backend.domain.cart.entity.Cart;
import com.example.backend.domain.cart.exception.CartErrorCode;
import com.example.backend.domain.cart.exception.CartException;
import com.example.backend.domain.cart.repository.CartRepository;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.product.service.ProductService;
import com.example.backend.global.auth.exception.AuthErrorCode;
import com.example.backend.global.auth.exception.AuthException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;
    private final CartConverter cartConverter;

    @Transactional
    public Long addCartItem(CartForm cartForm, Member member) {
        // 로그인한 회원과 요청한 회원이 다르면 exception 발생
        if (member.getId() != cartForm.memberId()) {
            throw new AuthException(AuthErrorCode.MEMBER_NOT_FOUND);
        }
        // 요청한 상품 ID와 수량
        Long productId = cartForm.productId();
        int quantity = cartForm.quantity();

        // 수량이 0 이하인 경우 exception 발생
        if (quantity <= 0) {
            throw new CartException(CartErrorCode.INVALID_QUANTITY);
        }

        // 이미 장바구니에 있는 상품인 경우 exception 발생
        if (cartRepository.existsByProductId_IdAndMemberId_Id(productId, member.getId())) {
            throw new CartException(CartErrorCode.ALREADY_EXISTS_IN_CART);
        }

        // 상품의 재고가 요청한 수량보다 적은 경우 exception 발생
        Product product = productService.findById(productId);

        if (quantity > product.getQuantity()) {
            throw new CartException(CartErrorCode.INSUFFICIENT_STOCK);
        }

        // 장바구니에 상품 추가
        Cart cart = cartConverter.toCart(cartForm, member, product);

        return cartRepository.save(cart).getId();
    }
}
