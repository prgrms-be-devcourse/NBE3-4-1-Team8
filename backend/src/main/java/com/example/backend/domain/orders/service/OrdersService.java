package com.example.backend.domain.orders.service;

import java.util.List;
import java.util.Optional;

import com.example.backend.domain.orders.converter.OrdersConverter;
import com.example.backend.domain.orders.dto.OrdersForm;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.product.exception.ProductErrorCode;
import com.example.backend.domain.product.exception.ProductException;
import com.example.backend.domain.product.repository.ProductRepository;
import com.example.backend.domain.productOrders.entity.ProductOrders;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.orders.dto.OrdersResponse;
import com.example.backend.domain.orders.entity.Orders;
import com.example.backend.domain.orders.exception.OrdersErrorCode;
import com.example.backend.domain.orders.exception.OrdersException;
import com.example.backend.domain.orders.repository.OrdersRepository;
import com.example.backend.domain.orders.status.DeliveryStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public OrdersResponse findOne(Long id) {
        Orders orders = ordersRepository.findOrderById(id)
                .orElseThrow(() -> new OrdersException(OrdersErrorCode.NOT_FOUND));

        return OrdersConverter.toOrdersResponse(orders);
    }

    @Transactional(readOnly = true)
    public List<OrdersResponse> current(Long id) {
        List<Orders> ordersList = Optional.ofNullable(
                ordersRepository.findByMemberIdAndDeliveryStatus(id, DeliveryStatus.READY)
        ).orElseThrow(() -> new OrdersException(OrdersErrorCode.NOT_FOUND));

        return OrdersConverter.toOrdersResponseList(ordersList);
    }


    @Transactional
    public Long create(OrdersForm ordersForm, Member member) {

        List<ProductOrders> productOrdersList = createProductOrdersList(ordersForm);

        Orders orders = OrdersConverter.toEntity(ordersForm, member, productOrdersList);

        return ordersRepository.save(orders).getId();
    }

    @Transactional(readOnly = true)
    public List<ProductOrders> createProductOrdersList(OrdersForm ordersForm) {
        return ordersForm.productOrdersRequestList().stream().map(
                po -> {
                    try {
                        Product product = productRepository.findById(po.productId())
                                .orElseThrow(() -> new ProductException(ProductErrorCode.NOT_FOUND));

                        return ProductOrders.create()
                                .product(product)
                                .price(product.getPrice())
                                .quantity(po.quantity())
                                .build();
                    } catch (ObjectOptimisticLockingFailureException e) {
                        throw new ProductException(ProductErrorCode.CONFLICT);
                    }
                }
        ).toList();
    }
}
