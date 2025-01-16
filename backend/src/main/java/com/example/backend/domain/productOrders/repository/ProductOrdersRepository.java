package com.example.backend.domain.productOrders.repository;

import com.example.backend.domain.productOrders.entity.ProductOrders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOrdersRepository extends JpaRepository<ProductOrders, Long> {
}
