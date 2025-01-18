package com.example.backend.domain.orders.converter;

import com.example.backend.domain.common.Address;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.orders.dto.OrdersForm;
import com.example.backend.domain.orders.dto.OrdersResponse;
import com.example.backend.domain.orders.dto.ProductInfoDto;
import com.example.backend.domain.orders.entity.Orders;
import com.example.backend.domain.productOrders.entity.ProductOrders;

import java.util.List;

public class OrdersConverter {

    public static Orders toEntity(OrdersForm ordersForm, Member member, List<ProductOrders> productOrdersList) {

        Address address = getAddress(ordersForm);

        return Orders.create()
                .member(member)
                .productOrdersList(productOrdersList)
                .address(address)
                .build();
    }

    private static Address getAddress(OrdersForm ordersForm) {
        Address address = Address.builder()
                .city(ordersForm.city())
                .district(ordersForm.district())
                .country(ordersForm.country())
                .detail(ordersForm.detail())
                .build();
        return address;
    }


    public static List<OrdersResponse> toOrdersResponseList(List<Orders> ordersList) {
        return ordersList.stream()
                .map(OrdersConverter::toOrdersResponse)
                .toList();
    }

    public static OrdersResponse toOrdersResponse(Orders orders) {
        return OrdersResponse.builder()
                .id(orders.getId())
                .products(toProductInfoDtos(orders))
                .totalPrice(orders.getTotalPrice())
                .createAt(orders.getCreatedAt())
                .modifiedAt(orders.getModifiedAt())
                .build();
    }

    private static List<ProductInfoDto> toProductInfoDtos(Orders orders) {
        return orders.getProductOrdersList().stream()
                .map(OrdersConverter::toProductInfoDto)
                .toList();
    }

    private static ProductInfoDto toProductInfoDto(ProductOrders productOrders) {
        return ProductInfoDto.builder()
                .id(productOrders.getProduct().getId())
                .name(productOrders.getProduct().getName())
                .price(productOrders.getPrice())
                .quantity(productOrders.getQuantity())
                .build();
    }
}