package com.example.backend.domain.cart.repository;

import com.example.backend.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    boolean existsByProductId_IdAndMemberId_Id(Long productId, Long memberId);
}
