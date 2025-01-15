package com.example.backend.domain.orders.service;

import com.example.backend.domain.orders.repository.OrderRepositoryTest;

import com.example.backend.domain.product.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class OrderServiceTest {


    @Autowired
    private OrderService orderService;


    @Autowired
    private ProductRepository productRepository;


}
