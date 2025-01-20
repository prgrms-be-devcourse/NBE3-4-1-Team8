package com.example.backend.domain.orders.converter;

import com.example.backend.domain.common.Address;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.orders.dto.OrdersForm;
import com.example.backend.domain.orders.dto.OrdersResponse;
import com.example.backend.domain.orders.dto.ProductInfoDto;
import com.example.backend.domain.orders.entity.Orders;
import com.example.backend.domain.productOrders.entity.ProductOrders;

import java.util.Comparator;
import java.util.List;

public class OrdersConverter {

    public static Orders of(OrdersForm ordersForm, Member member, List<ProductOrders> productOrdersList) {
        Address address = toAddress(ordersForm);
        return Orders.create()
                .member(member)
                .productOrdersList(productOrdersList)
                .address(address)
                .build();
    }

    private static Address toAddress(OrdersForm ordersForm) {
        return Address.builder()
                .city(ordersForm.city())
                .district(ordersForm.district())
                .country(ordersForm.country())
                .detail(ordersForm.detail())
                .build();
    }

    public static List<OrdersResponse> from(List<Orders> ordersList) {
        return ordersList.stream()
                .map(OrdersConverter::toResponse)
                .sorted(Comparator.comparing(OrdersResponse::modifiedAt).reversed())
                .toList();
    }

    public static OrdersResponse toResponse(Orders orders) {
        return OrdersResponse.builder()
                .id(orders.getId())
                .products(toProductInfoDtoList(orders))
                .totalPrice(orders.getTotalPrice())
                .status(orders.getDeliveryStatus())
                .createAt(orders.getCreatedAt())
                .modifiedAt(orders.getModifiedAt())
                .build();
    }

    private static List<ProductInfoDto> toProductInfoDtoList(Orders orders) {
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