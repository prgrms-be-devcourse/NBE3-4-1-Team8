package com.example.backend.domain.productOrders.repository;

import com.example.backend.domain.productOrders.entity.ProductOrders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOrdersRepository extends JpaRepository<ProductOrders, Long> {

    /**
     * 상품 id로 해당 상품 주문 내역 존재 여부 검증 메서드
     * @param id
     * @return boolean
     */
    boolean existsByProductId(Long id);
}
