package com.example.backend.domain.orders.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.domain.common.Address;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.entity.MemberStatus;
import com.example.backend.domain.member.entity.Role;
import com.example.backend.domain.member.repository.MemberRepository;
import com.example.backend.domain.orders.entity.Orders;
import com.example.backend.domain.orders.status.DeliveryStatus;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.product.repository.ProductRepository;
import com.example.backend.domain.productOrders.entity.ProductOrders;
import com.example.backend.domain.productOrders.repository.ProductOrdersRepository;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Transactional
@DataJpaTest
@Slf4j
public class OrdersRepositoryTest {

	@Autowired
	OrdersRepository ordersRepository;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	ProductOrdersRepository productOrdersRepository;
	@Autowired
	EntityManager entityManager;

	private Member createMember() {
		return Member.builder()
			.username("test")
			.nickname("test")
			.password("123")
			.role(Role.ROLE_USER)
			.memberStatus(MemberStatus.ACTIVE)
			.address(new Address("123 Main St", "New York", "NY", "10001"))
			.createdAt(ZonedDateTime.now())
			.modifiedAt(ZonedDateTime.now())
			.build();
	}

	private Product createProduct() {
		return Product.builder()
			.name("test")
			.content("test")
			.price(100)
			.imgUrl("test")
			.quantity(10)
			.build();
	}

	private ProductOrders createProductOrders(Product product) {
		return ProductOrders.create()
			.product(product)
			.quantity(2)
			.price(100)
			.build();
	}

	@Test
	@DisplayName("주문 저장 성공")
	void saveOrder() {

		// Member 객체 생성

		Member savedMember = memberRepository.save(createMember());

		// Product 객체 생성
		Product savedProduct = productRepository.save(createProduct());

		// ProductOrders 객체 생성
		ProductOrders savedProductOrders = productOrdersRepository.save(createProductOrders(savedProduct));

		// ProductOrders 목록 준비
		List<ProductOrders> productOrdersList = new ArrayList<>();
		productOrdersList.add(savedProductOrders);

		// Orders 객체 생성
		Orders orders = Orders.create()
			.member(savedMember)
			.productOrdersList(productOrdersList)
			.address(savedMember.getAddress())
			.build();

		// 주문 저장
		Orders savedOrder = ordersRepository.save(orders);

		Address address = new Address("123 Main St", "New York", "NY", "10001");

		// 저장된 주문이 예상한 주문과 동일한지 검증
		assertThat(orders).isEqualTo(savedOrder);
		assertThat(orders.getMember()).isEqualTo(savedOrder.getMember());
		assertThat(orders.getProductOrdersList()).isEqualTo(savedOrder.getProductOrdersList());
		assertThat(200).isEqualTo(savedOrder.getTotalPrice());
		assertThat(address.getCity()).isEqualTo(savedOrder.getAddress().getCity());
		assertThat(address.getDistrict()).isEqualTo(savedOrder.getAddress().getDistrict());
		assertThat(address.getCountry()).isEqualTo(savedOrder.getAddress().getCountry());
		assertThat(address.getDetail()).isEqualTo(savedOrder.getAddress().getDetail());

	}

	@Test
	@DisplayName("현재 주문 조회 성공")
	void findByMemberIdAndDeliveryStatus() {
		Member savedMember = memberRepository.save(createMember());
		log.info("memberId = {}", savedMember.getId()); // memberId: 1 반환

		Product savedProduct = productRepository.save(createProduct());

		ProductOrders savedProductOrders = productOrdersRepository.save(createProductOrders(savedProduct));

		Orders orders = Orders.create()
			.member(savedMember)
			.productOrdersList(List.of(savedProductOrders))
			.address(savedMember.getAddress())
			.build();

		Orders save = ordersRepository.save(orders);

		List<Orders> ordersList =
			ordersRepository.findByMemberIdAndDeliveryStatus(save.getMember().getId(), DeliveryStatus.READY);

		log.info("orderList = {}", ordersList);

		log.info("memberId = {}", save.getMember().getId()); // 이것조차 이상 없음 memberId 는 잘 저장됨

		assertThat(ordersList.size()).isEqualTo(1);
		assertThat(ordersList.get(0).getDeliveryStatus()).isEqualTo(DeliveryStatus.READY);
		assertThat(ordersList.get(0).getMember().getUsername()).isEqualTo("test");
	}

	@Test
	@DisplayName("주문 상태가 READY 인 주문만 조회")
	void getOnlyStatusReady() {

		Member savedMember = memberRepository.save(createMember());
		Product savedProduct = productRepository.save(createProduct());

		Orders orders1 = Orders.create()
			.member(savedMember)
			.productOrdersList(List.of(createProductOrders(savedProduct)))
			.address(savedMember.getAddress())
			.build();

		Orders orders2 = Orders.create()
			.member(savedMember)
			.productOrdersList(List.of(createProductOrders(savedProduct)))
			.address(savedMember.getAddress())
			.build();

		orders2.changeStatus(DeliveryStatus.SHIPPED);

		ordersRepository.save(orders1);
		ordersRepository.save(orders2);

		ordersRepository.flush();
		entityManager.clear();

		List<Orders> ordersList = ordersRepository.findByMemberIdAndDeliveryStatus(
			savedMember.getId(),
			DeliveryStatus.READY
		);

		assertThat(ordersList.size()).isEqualTo(1);
		assertThat(ordersList.get(0).getDeliveryStatus()).isEqualTo(DeliveryStatus.READY);
	}

	@Test
	@DisplayName("모든 주문 목록 조회 및 수정시간순으로 조회 성공")
	void history() {
		Member savedMember = memberRepository.save(createMember());
		Product savedProduct = productRepository.save(createProduct());

		Orders orders1 = Orders.create()
			.member(savedMember)
			.productOrdersList(List.of(createProductOrders(savedProduct)))
			.address(savedMember.getAddress())
			.build();

		Orders orders2 = Orders.create()
			.member(savedMember)
			.productOrdersList(List.of(createProductOrders(savedProduct)))
			.address(savedMember.getAddress())
			.build();

		orders2.changeStatus(DeliveryStatus.SHIPPED);

		ordersRepository.save(orders1);
		ordersRepository.save(orders2);

		List<Orders> ordersList = ordersRepository.findAllByMemberIdAndDeliveryStatusOrderByModifiedAt(
			savedMember.getId(),
			List.of(DeliveryStatus.READY,
				DeliveryStatus.SHIPPED)
		);

		assertThat(ordersList.size()).isEqualTo(2);
		assertThat(ordersList.get(0).getDeliveryStatus()).isEqualTo(DeliveryStatus.SHIPPED);
	}

	@Test
	@DisplayName("startTime, endTime 사이의 배송 준비중인 데이터 조회")
	void findReadyOrders() {
		//given
		Member savedMember = memberRepository.save(createMember());
		Product savedProduct = productRepository.save(createProduct());

		ProductOrders productOrders1 = createProductOrders(savedProduct);
		ProductOrders productOrders2 = createProductOrders(savedProduct);
		ProductOrders productOrders3 = createProductOrders(savedProduct);

		Orders createOrders1 = Orders.create()
			.member(savedMember)
			.address(savedMember.getAddress())
			.productOrdersList(List.of(productOrders1, productOrders2, productOrders3))
			.build();

		Orders createOrders2 = Orders.create()
			.member(savedMember)
			.address(savedMember.getAddress())
			.productOrdersList(List.of(productOrders1, productOrders2, productOrders3))
			.build();

		Orders createOrders3 = Orders.create()
			.member(savedMember)
			.address(savedMember.getAddress())
			.productOrdersList(List.of(productOrders1, productOrders2, productOrders3))
			.build();

		createOrders3.changeStatus(DeliveryStatus.SHIPPED);

		ordersRepository.saveAll(List.of(createOrders1, createOrders2, createOrders3));

		ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime startTime = now.minusDays(1).with(LocalTime.of(14, 0));
		ZonedDateTime endTime = now;

		//when
		List<Orders> ordersList = ordersRepository.findReadyOrders(startTime, endTime);

		//then
		Orders orders1 = ordersList.get(0);
		Orders orders2 = ordersList.get(1);

		assertThat(ordersList.size()).isEqualTo(2);
		assertThat(orders1).isEqualTo(createOrders1);
		assertThat(orders2).isEqualTo(createOrders2);
		assertThat(orders1.getModifiedAt()).isAfter(startTime);
		assertThat(orders1.getModifiedAt()).isBefore(endTime);
		assertThat(orders2.getModifiedAt()).isAfter(startTime);
		assertThat(orders2.getModifiedAt()).isBefore(endTime);
	}

	@Test
	@DisplayName("startTime, endTime 사이의 주문 배송 상태 SHIPPED으로 변경")
	void bulkUpdateDeliveryStatus() {
		//given
		Member savedMember = memberRepository.save(createMember());
		Product savedProduct = productRepository.save(createProduct());

		ProductOrders productOrders1 = createProductOrders(savedProduct);
		ProductOrders productOrders2 = createProductOrders(savedProduct);
		ProductOrders productOrders3 = createProductOrders(savedProduct);

		Orders createOrders1 = Orders.create()
			.member(savedMember)
			.address(savedMember.getAddress())
			.productOrdersList(List.of(productOrders1, productOrders2, productOrders3))
			.build();

		Orders createOrders2 = Orders.create()
			.member(savedMember)
			.address(savedMember.getAddress())
			.productOrdersList(List.of(productOrders1, productOrders2, productOrders3))
			.build();

		Orders createOrders3 = Orders.create()
			.member(savedMember)
			.address(savedMember.getAddress())
			.productOrdersList(List.of(productOrders1, productOrders2, productOrders3))
			.build();

		createOrders3.changeStatus(DeliveryStatus.SHIPPED);

		ordersRepository.saveAll(List.of(createOrders1, createOrders2, createOrders3));

		ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime startTime = now.minusDays(1).with(LocalTime.of(14, 0));
		ZonedDateTime endTime = now;

		//when
		ordersRepository.bulkUpdateDeliveryStatus(startTime, endTime);

		//then
		entityManager.flush();
		entityManager.clear();
		List<Orders> ordersList = ordersRepository.findAll();

		assertThat(ordersList.size()).isEqualTo(3);
		assertThat(ordersList).allMatch(order ->
			order.getDeliveryStatus() == DeliveryStatus.SHIPPED &&
			order.getModifiedAt().isAfter(startTime) &&
			order.getModifiedAt().isBefore(endTime)
		);
	}

	@Test
	@DisplayName("startTime, endTime 사이의 배송 준비중인 회원 username 조회")
	void findUsernameByReady() {
		//given
		Member savedMember = memberRepository.save(createMember());
		Product savedProduct = productRepository.save(createProduct());

		ProductOrders productOrders1 = createProductOrders(savedProduct);
		ProductOrders productOrders2 = createProductOrders(savedProduct);
		ProductOrders productOrders3 = createProductOrders(savedProduct);

		Orders createOrders1 = Orders.create()
			.member(savedMember)
			.address(savedMember.getAddress())
			.productOrdersList(List.of(productOrders1, productOrders2, productOrders3))
			.build();

		Orders createOrders2 = Orders.create()
			.member(savedMember)
			.address(savedMember.getAddress())
			.productOrdersList(List.of(productOrders1, productOrders2, productOrders3))
			.build();

		Orders createOrders3 = Orders.create()
			.member(savedMember)
			.address(savedMember.getAddress())
			.productOrdersList(List.of(productOrders1, productOrders2, productOrders3))
			.build();

		createOrders3.changeStatus(DeliveryStatus.SHIPPED);

		ordersRepository.saveAll(List.of(createOrders1, createOrders2, createOrders3));

		ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime startTime = now.minusDays(1).with(LocalTime.of(14, 0));
		ZonedDateTime endTime = now;

		//when
		List<String> usernameList = ordersRepository.findUsernameByReady(startTime, endTime);

		//then
		String username1 = usernameList.get(0);
		String username2 = usernameList.get(1);

		assertThat(usernameList.size()).isEqualTo(2);
		assertThat(username1).isEqualTo(createOrders1.getMember().getUsername());
		assertThat(username2).isEqualTo(createOrders2.getMember().getUsername());
	}

}
