package com.example.backend.domain.product.controller;

import com.example.backend.domain.product.dto.ProductForm;
import com.example.backend.domain.product.dto.ProductResponse;
import com.example.backend.domain.product.service.ProductService;
import com.example.backend.global.response.GenericResponse;
import com.example.backend.global.validation.ValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
    public ResponseEntity<GenericResponse<ProductResponse>> findById(@PathVariable("id") Long id) {

        ProductResponse productResponse = productService.findProductResponseById(id);

        return ResponseEntity.ok().body(GenericResponse.of(productResponse));
    }

    @GetMapping
    public ResponseEntity<GenericResponse<Page<ProductResponse>>> findAllPage(
            @RequestParam(value = "page", defaultValue = "0") int page) {

        Page<ProductResponse> productResponsePage = productService.findAllPage(page);

        return ResponseEntity.ok().body(GenericResponse.of(productResponsePage));
    }

    @PostMapping
    public ResponseEntity<GenericResponse<String>> create(@RequestBody @Validated(ValidationSequence.class) ProductForm productForm) {

        productService.create(productForm);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GenericResponse.of("상품이 정상적으로 등록되었습니다."));
    }

}
