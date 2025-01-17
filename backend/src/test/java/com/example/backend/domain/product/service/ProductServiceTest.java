package com.example.backend.domain.product.service;

import com.example.backend.domain.product.dto.ProductForm;
import com.example.backend.domain.product.dto.ProductResponse;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.product.exception.ProductException;
import com.example.backend.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * ProductServiceTest
 * ProductService 테스트 클래스
 *
 * @author 100minha
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private final String name1 = "Test Product Name";
    private final String content1 = "Test Product Description";
    private final int price1 = 1000;
    private final String imgUrl1 = "Test Product Image";
    private final int quantity1 = 10;

    ProductForm productForm1 = new ProductForm(name1, content1, price1, imgUrl1, quantity1);
    Product product1 = Product.builder()
            .name(name1)
            .content(content1)
            .price(price1)
            .imgUrl(imgUrl1)
            .quantity(quantity1)
            .build();

    @Test
    @DisplayName("상품 등록 테스트")
    void createTest() {
        // given
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        // when
        productService.create(productForm1);

        // then
        verify(productRepository, times(1)).save(productCaptor.capture());
        Product savedProduct = productCaptor.getValue();

        assertThat(name1).isEqualTo(savedProduct.getName());
        assertThat(content1).isEqualTo(savedProduct.getContent());
        assertThat(price1).isEqualTo(savedProduct.getPrice());
        assertThat(imgUrl1).isEqualTo(savedProduct.getImgUrl());
        assertThat(quantity1).isEqualTo(savedProduct.getQuantity());
    }

    @Test
    @DisplayName("상품 단건 조회(Entity) 성공 테스트")
    void findByIdSuccessTest() {
        // given
        Long id = 1L;
        when(productRepository.findById(id)).thenReturn(Optional.of(product1));

        // when
        Product product = productService.findById(id);

        // then
        assertThat(product.getName()).isEqualTo(this.name1);
        assertThat(product.getContent()).isEqualTo(this.content1);
        assertThat(product.getPrice()).isEqualTo(this.price1);
        assertThat(product.getImgUrl()).isEqualTo(this.imgUrl1);
        assertThat(product.getQuantity()).isEqualTo(this.quantity1);
    }

    @Test
    @DisplayName("상품 단건 조회(Entity) 실패 테스트")
    void findByIdFailTest() {
        // given
        Long invalidId = 999L; // 존재하지 않는 상품 ID
        when(productRepository.findById(invalidId)).thenReturn(Optional.empty());

        // when
        ProductException exception = assertThrows(
                ProductException.class,
                () -> productService.findById(invalidId)
        );

        // then
        assertThat(exception.getCode()).isEqualTo("404");
    }

    @Test
    @DisplayName("상품 단건 조회(DTO) 테스트")
    void findProductResponseByIdSuccessTest() {
        // given
        Long id = 1L;
        when(productRepository.findProductResponseById(id)).thenReturn(Optional.of(ProductResponse.of(product1)));

        // when
        ProductResponse productResponse = productService.findProductResponseById(1L);

        // then
        assertThat(productResponse.name()).isEqualTo(this.name1);
        assertThat(productResponse.content()).isEqualTo(this.content1);
        assertThat(productResponse.price()).isEqualTo(this.price1);
        assertThat(productResponse.imgUrl()).isEqualTo(this.imgUrl1);
    }

    @Test
    @DisplayName("상품 단건 조회(DTO) 실패 테스트")
    void findProductResponseByIdFailTest() {
        // given
        Long invalidId = 999L; // 존재하지 않는 상품 ID
        when(productRepository.findProductResponseById(invalidId)).thenReturn(Optional.empty());

        // when
        ProductException exception = assertThrows(
                ProductException.class,
                () -> productService.findProductResponseById(invalidId)
        );

        // then
        assertThat(exception.getCode()).isEqualTo("404");
    }

    @Test
    @DisplayName("상품 다건 조회 테스트")
    void findAllPagedTest() {
        // given
        List<ProductResponse> productResponseList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            productResponseList.add(ProductResponse.of(Product.builder()
                    .name("Test Name_" + i)
                    .build()));
        }

        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductResponse> mockPage = new PageImpl<>(productResponseList, pageable, 5);
        when(productRepository.findAllPaged(any())).thenReturn(mockPage);

        // when
        Page<ProductResponse> productResponsePage = productService.findAllPaged(pageable);

        //then
        verify(productRepository, times(1)).findAllPaged(pageable);

        assertThat(productResponsePage.getTotalPages()).isEqualTo(1);
        assertThat(productResponsePage.getNumberOfElements()).isEqualTo(5);
        assertThat(productResponsePage.getContent().get(3).name()).isEqualTo("Test Name_4");
    }

    @Test
    @DisplayName("상품 다건 조회 빈 페이지 반환 시 404반환 테스트")
    void findAllPagedButIsEmptyTest() {
        // given
        Pageable inValidPageable = PageRequest.of(0, 999);  //빈 페이지 요청
        when(productRepository.findAllPaged(inValidPageable)).thenReturn(Page.empty());

        // when
        ProductException exception = assertThrows(
                ProductException.class,
                () -> productService.findAllPaged(inValidPageable)
        );

        //then
        verify(productRepository, times(1)).findAllPaged(inValidPageable);

        assertThat(exception.getCode()).isEqualTo("404");
    }

}