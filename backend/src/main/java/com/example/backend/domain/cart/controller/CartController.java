package com.example.backend.domain.cart.controller;

import com.example.backend.domain.cart.dto.CartForm;
import com.example.backend.domain.cart.dto.CartResponse;
import com.example.backend.domain.cart.dto.CartUpdateForm;
import com.example.backend.domain.cart.service.CartService;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.global.auth.model.CustomUserDetails;
import com.example.backend.global.response.GenericResponse;
import com.example.backend.global.validation.ValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<GenericResponse<Long>> addCartItem(
            @RequestBody @Validated(ValidationSequence.class) CartForm cartForm,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Member member = customUserDetails.getMember();

        Long cartId = cartService.addCartItem(cartForm, member);

        return ResponseEntity.status(HttpStatus.CREATED).body(GenericResponse.of(cartId));
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<CartResponse>>> getCart(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Member member = customUserDetails.getMember();

        List<CartResponse> cartResponses = cartService.getCartByMember(member);

        return ResponseEntity.status(HttpStatus.OK).body(GenericResponse.of(cartResponses));
    }

    @PatchMapping
    public ResponseEntity<GenericResponse<Long>> updateCartItemQuantity(
            @RequestBody @Validated(ValidationSequence.class) CartUpdateForm cartUpdateForm,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Member member = customUserDetails.getMember();
        Long cartId = cartService.updateCartItemQuantity(cartUpdateForm, member);

        return ResponseEntity.status(HttpStatus.OK).body(GenericResponse.of(cartId));
    }

}
