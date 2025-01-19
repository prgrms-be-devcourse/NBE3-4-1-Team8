package com.example.backend.domain.cart.repository;

import com.example.backend.domain.cart.entity.Cart;
import com.example.backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    boolean existsByProductIdAndMemberId(Long productId, Long memberId);

    @Query("SELECT c FROM Cart c JOIN FETCH c.product WHERE c.member = :member")
    List<Cart> findAllByMemberWithProducts(@Param("member") Member member);

    void deleteByMemberId(Long memberId);

    Optional<Cart> findByProductIdAndMemberId(Long productId, Long memberId);
}
