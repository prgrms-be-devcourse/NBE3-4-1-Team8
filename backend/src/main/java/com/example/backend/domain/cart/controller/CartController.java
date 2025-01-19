package com.example.backend.domain.cart.controller;

import com.example.backend.domain.cart.dto.CartForm;
import com.example.backend.domain.cart.service.CartService;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.global.auth.model.CustomUserDetails;
import com.example.backend.global.response.GenericResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<GenericResponse<Long>> addCartItem(
            @RequestBody @Valid CartForm cartForm,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Member member = customUserDetails.getMember();

        Long cartId = cartService.addCartItem(cartForm, member);

        return ResponseEntity.status(HttpStatus.CREATED).body(GenericResponse.of(cartId));

    }
}
