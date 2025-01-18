package com.example.backend.domain.orders.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.backend.domain.common.Address;
import com.example.backend.domain.orders.dto.OrdersForm;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.product.exception.ProductErrorCode;
import com.example.backend.domain.product.exception.ProductException;
import com.example.backend.domain.product.repository.ProductRepository;
import com.example.backend.domain.productOrders.entity.ProductOrders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

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

    public List<OrdersResponse> current(Long id) {

        List<Orders> ordersList = Optional.ofNullable(
                ordersRepository.findByMemberIdAndDeliveryStatus(id, DeliveryStatus.READY)
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

    public Long create(OrdersForm ordersForm, Member member) {

        List<ProductOrders> productOrdersList = ordersForm.productOrdersRequestList().stream().map(
                po -> {
                    Product product = productRepository.findById(po.productId())
                            .orElseThrow(() -> new ProductException(ProductErrorCode.NOT_FOUND));

                    return ProductOrders.create()
                            .product(product)
                            .price(product.getPrice())
                            .quantity(po.quantity())
                            .build();
                }
        ).toList();

        Address address = Address.builder()
                .city(ordersForm.city())
                .district(ordersForm.district())
                .country(ordersForm.country())
                .detail(ordersForm.detail())
                .build();

        Orders orders = Orders.create()
                .member(member)
                .productOrdersList(productOrdersList)
                .address(address)
                .build();

        return ordersRepository.save(orders).getId();

    }

    private Member getMember(String username) {
       return memberRepository.findByUsername(username)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private List<ProductInfoDto> toProductInfoDtos(Orders orders) {
        List<ProductInfoDto> productInfoDtoList = orders.getProductOrdersList().stream()
                .map(po ->
                        ProductInfoDto.builder()
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
