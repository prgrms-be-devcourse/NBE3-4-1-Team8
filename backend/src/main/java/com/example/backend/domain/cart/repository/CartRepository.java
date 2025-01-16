package com.example.backend.domain.cart.repository;

import com.example.backend.domain.cart.entity.Cart;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByMemberIdAndProductId(Member member, Product product);
}
