package com.example.backend.domain.orders.repository;

import com.example.backend.domain.orders.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    @Query("select distinct o from Orders o " +
            "join fetch o.member m " +
            "join fetch o.productOrders po " +
            "join fetch po.product p " +
            "where o.id = :id")
    Optional<Orders> findOrderById(@Param("id") Long id);
}
