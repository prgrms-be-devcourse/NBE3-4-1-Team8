package com.example.backend.domain.orders.repository;

import com.example.backend.domain.orders.entity.Orders;
import com.example.backend.domain.orders.status.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    @Query("select distinct o from Orders o " +
            "join fetch o.member m " +
            "join fetch o.productOrdersList po " +
            "join fetch po.product p " +
            "where o.id = :id")
    Optional<Orders> findOrderById(@Param("id") Long id);

    @Query("select distinct o from Orders o " +
            "join fetch o.member m " +
            "join fetch o.productOrdersList po " +
            "join fetch po.product p " +
            "where m.id = :id and o.deliveryStatus = :status")
    List<Orders> findByMemberIdAndDeliveryStatus(
            @Param("id") Long id,
            @Param("status") DeliveryStatus status);

    @Query("select distinct o from Orders o " +
            "join fetch o.member m " +
            "join fetch o.productOrdersList po " +
            "join fetch po.product p " +
            "where m.id = :id and o.deliveryStatus in :status " +
            "order by o.modifiedAt desc")
    List<Orders> findAllByMemberIdAndDeliveryStatusOrderByModifiedAt(
            @Param("id") Long id,
            @Param("status") List<DeliveryStatus> status);



}
