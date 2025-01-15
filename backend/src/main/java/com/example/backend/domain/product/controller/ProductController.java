package com.example.backend.domain.product.controller;

import com.example.backend.domain.product.dto.ProductResponse;
import com.example.backend.domain.product.service.ProductService;
import com.example.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ProductController
 * "/products"로 들어오는 요청을 처리하는 컨트롤러
 * @author 100minha
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<ProductResponse>> findById(@PathVariable Long id) {

        ProductResponse productResponse = productService.findProductResponseById(id);

        return ResponseEntity.ok().body(GenericResponse.of(productResponse));
    }

}