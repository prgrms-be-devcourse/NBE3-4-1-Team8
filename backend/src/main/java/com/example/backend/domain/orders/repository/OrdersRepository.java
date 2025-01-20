package com.example.backend.domain.orders.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.domain.orders.entity.Orders;
import com.example.backend.domain.orders.status.DeliveryStatus;

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

	void deleteByMemberId(Long memberId);

	/**
	 * 배송 상태가 READY이며  modifiedAt가 startTime, endTime 사이인 주문을 조회합니다.
	 * @param startTime
	 * @param endTime
	 * @return {@link List<Orders>}
	 */
	@Query("""
		      SELECT o FROM Orders o
		WHERE o.modifiedAt >= :startTime
		AND o.modifiedAt < :endTime
		AND o.deliveryStatus = 'READY'
		   """)
	List<Orders> findReadyOrders(@Param("startTime") ZonedDateTime startTime, @Param("endTime") ZonedDateTime endTime);

	/**
	 * 배송 상태가 READY이며 modifiedAt가 startTime, endTime 사이인 <br>
	 * 주문의 배송 상태를 SHIPPED로 변경합니다.
	 * @param startTime
	 * @param endTime
	 */
	@Modifying
	@Query("""
		UPDATE Orders o
		SET o.deliveryStatus = 'SHIPPED'
		WHERE CAST(o.modifiedAt AS timestamp) >= CAST(:startTime AS timestamp)
		AND CAST(o.modifiedAt AS timestamp) < CAST(:endTime AS timestamp)
		AND o.deliveryStatus = 'READY'
		""")
	void bulkUpdateDeliveryStatus(@Param("startTime") ZonedDateTime startTime,
		@Param("endTime") ZonedDateTime endTime);
}
