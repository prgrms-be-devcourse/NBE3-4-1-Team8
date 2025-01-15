package com.example.backend.domain.orders.controller;


import com.example.backend.domain.orders.dto.OrderResponse;
import com.example.backend.domain.orders.service.OrderService;
import com.example.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<OrderResponse>> findOne(
            @PathVariable(name = "id") Long id
    ) {
        OrderResponse response = orderService.findOne(id);

        return ResponseEntity.ok()
                .body(GenericResponse.of(response));
    }

}
