package com.example.backend.domain.orders.controller;


import com.example.backend.domain.orders.dto.OrdersResponse;
import com.example.backend.domain.orders.service.OrdersService;
import com.example.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrdersController {

    private final OrdersService ordersService;

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<OrdersResponse>> findOne(
            @PathVariable(name = "id") Long id
    ) {
        OrdersResponse response = ordersService.findOne(id);

        return ResponseEntity.ok()
                .body(GenericResponse.of(response));
    }

    @GetMapping("/current")
    public ResponseEntity<GenericResponse<List<OrdersResponse>>> current(
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        String username = userDetails.getUsername();
        List<OrdersResponse> responseList = ordersService.current(username);

        return ResponseEntity.ok()
                .body(GenericResponse.of(responseList));

    }

}
