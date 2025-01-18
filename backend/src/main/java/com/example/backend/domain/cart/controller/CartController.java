package com.example.backend.domain.cart.controller;

import com.example.backend.domain.cart.dto.CartForm;
import com.example.backend.domain.cart.dto.CartResponse;
import com.example.backend.domain.cart.service.CartService;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.global.auth.model.CustomUserDetails;
import com.example.backend.global.response.GenericResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<List<CartResponse>>> getCarts(
            @PathVariable("id") Long memberId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Member member = customUserDetails.getMember();

        List<CartResponse> cartResponses = cartService.getCartsByMember(memberId, member);

        return ResponseEntity.ok(GenericResponse.of(cartResponses));
    }
}
