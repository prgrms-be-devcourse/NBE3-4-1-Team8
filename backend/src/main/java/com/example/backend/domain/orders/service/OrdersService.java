package com.example.backend.domain.orders.service;


import com.example.backend.domain.orders.dto.OrdersResponse;
import com.example.backend.domain.orders.dto.ProductInfoDto;
import com.example.backend.domain.orders.entity.Orders;
import com.example.backend.domain.orders.exception.OrdersErrorCode;
import com.example.backend.domain.orders.exception.OrdersException;
import com.example.backend.domain.orders.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrdersService {

    private final OrdersRepository ordersRepository;

    public OrdersResponse findOne(Long id) {
        Orders orders = ordersRepository.findOrderById(id)
                .orElseThrow(() -> new OrdersException(OrdersErrorCode.NOT_FOUND));

        // map 으로 돌려서 <List>ProductInfoDto 로 변환 후 Response 에 저장
        List<ProductInfoDto> productInfoDtoList = toProductInfoDtos(orders);

        return OrdersResponse.builder()
                .id(orders.getId())
                .products(productInfoDtoList)
                .totalPrice(orders.getTotalPrice())
                .status(orders.getDeliveryStatus())
                .createAt(orders.getCreatedAt())
                .modifiedAt(orders.getModifiedAt())
                .build();

    }

    private List<ProductInfoDto> toProductInfoDtos(Orders orders) {
        List<ProductInfoDto> productInfoDtoList = orders.getProductOrders().stream()
                .map(po -> ProductInfoDto.builder()
                        .id(po.getId())
                        .name(po.getProduct().getName())
                        .price(po.getPrice())
                        .ima_url(po.getProduct().getImgUrl())
                        .quantity(po.getQuantity())
                        .build())
                .collect(Collectors.toList());
        return productInfoDtoList;
    }
}
