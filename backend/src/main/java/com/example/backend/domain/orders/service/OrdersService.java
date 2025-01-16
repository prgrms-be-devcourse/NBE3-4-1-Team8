package com.example.backend.domain.orders.service;


import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.exception.MemberErrorCode;
import com.example.backend.domain.member.exception.MemberException;
import com.example.backend.domain.member.repository.MemberRepository;
import com.example.backend.domain.orders.dto.OrdersResponse;
import com.example.backend.domain.orders.dto.ProductInfoDto;
import com.example.backend.domain.orders.entity.Orders;
import com.example.backend.domain.orders.exception.OrdersErrorCode;
import com.example.backend.domain.orders.exception.OrdersException;
import com.example.backend.domain.orders.repository.OrdersRepository;
import com.example.backend.domain.orders.status.DeliveryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final MemberRepository memberRepository;

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

    public List<OrdersResponse> current(String username) {
        Member member = getMember(username);

        List<Orders> ordersList = Optional.ofNullable(
                ordersRepository.findByMemberIdAndDeliveryStatus(member.getId(), DeliveryStatus.READY)
        ).orElseThrow(() -> new OrdersException(OrdersErrorCode.NOT_FOUND));

        List<OrdersResponse> responseList = ordersList.stream().map(
                o -> OrdersResponse.builder()
                        .id(o.getId())
                        .products(toProductInfoDtos(o))
                        .totalPrice(o.getTotalPrice())
                        .createAt(o.getCreatedAt())
                        .modifiedAt(o.getModifiedAt())
                        .build()
        ).collect(Collectors.toList());

        return responseList;
    }

    private Member getMember(String username) {
       return memberRepository.findByUsername(username)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));
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
