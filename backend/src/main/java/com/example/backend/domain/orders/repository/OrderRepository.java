package com.example.backend.domain.orders.repository;

import com.example.backend.domain.orders.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
