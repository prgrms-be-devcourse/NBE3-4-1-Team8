package com.example.backend.domain.product.service;

import com.example.backend.domain.product.converter.ProductConverter;
import com.example.backend.domain.product.dto.ProductForm;
import com.example.backend.domain.product.dto.ProductResponse;
import com.example.backend.domain.product.entity.Product;
import com.example.backend.domain.product.exception.ProductException;
import com.example.backend.domain.product.repository.ProductRepository;
import com.example.backend.domain.productOrders.repository.ProductOrdersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

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
    @Mock
    private ProductOrdersRepository productOrdersRepository;

    @InjectMocks
    private ProductService productService;

    private final String name1 = "Test Product Name";
    private final String content1 = "Test Product Description";
    private final int price1 = 1000;
    private final String imgUrl1 = "Test Product Image";
    private final int quantity1 = 10;

    ProductForm productForm1 = ProductForm.builder()
            .name(name1)
            .content(content1)
            .price(price1)
            .imgUrl(imgUrl1)
            .quantity(quantity1)
            .build();
    Product product1 = ProductConverter.from(productForm1);

    @Test
    @DisplayName("상품 등록 테스트")
    void createTest() {
        // given
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        when(productRepository.existsByName(productForm1.name())).thenReturn(false);

        // when
        productService.create(productForm1);

        // then
        verify(productRepository, times(1)).save(productCaptor.capture());
        Product savedProduct = productCaptor.getValue();

        assertThat(savedProduct.getName()).isEqualTo(name1);
        assertThat(savedProduct.getContent()).isEqualTo(content1);
        assertThat(savedProduct.getPrice()).isEqualTo(price1);
        assertThat(savedProduct.getImgUrl()).isEqualTo(imgUrl1);
        assertThat(savedProduct.getQuantity()).isEqualTo(quantity1);
    }

    @Test
    @DisplayName("중복 이름 상품 등록 테스트")
    void alreadyExistsCreateTest() {
        // given
        when(productRepository.existsByName(productForm1.name())).thenReturn(true);

        // when
        ProductException exception = assertThrows(
                ProductException.class,
                () -> productService.create(productForm1)
        );

        // then
        verify(productRepository, times(1)).existsByName(productForm1.name());
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getCode()).isEqualTo("400-2");
        assertThat(exception.getMessage()).isEqualTo("중복된 상품 이름입니다.");
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
        when(productRepository.findProductResponseById(id)).thenReturn(Optional.of(ProductConverter.from(product1)));

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
        Sort sortByNameAsc = Sort.by(Sort.Order.asc("name"));
        List<ProductResponse> productResponseList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            productResponseList.add(ProductConverter.from(Product.builder()
                    .name("Test Name_" + i)
                    .build()));
        }

        Pageable pageable = PageRequest.of(0, 10, sortByNameAsc);
        Page<ProductResponse> mockPage = new PageImpl<>(productResponseList, pageable, 5);
        when(productRepository.findAllPaged(any())).thenReturn(mockPage);

        // when
        Page<ProductResponse> productResponsePage = productService.findAllPaged(0);

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
        Sort sortByNameAsc = Sort.by(Sort.Order.asc("name"));
        Pageable inValidPageable = PageRequest.of(999, 10, sortByNameAsc);  //빈 페이지 요청
        when(productRepository.findAllPaged(inValidPageable)).thenReturn(Page.empty());

        // when
        ProductException exception = assertThrows(
                ProductException.class,
                () -> productService.findAllPaged(999)
        );

        //then
        verify(productRepository, times(1)).findAllPaged(inValidPageable);

        assertThat(exception.getCode()).isEqualTo("404");
    }

    @Test
    @DisplayName("상품 수정 테스트(더티체킹)")
    void modifyTest() {
        // given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        ProductForm updatedproductForm = ProductForm.builder()
                .name("Updated Name")
                .content("Updated Content")
                .price(12345)
                .imgUrl("Updated imgUrl")
                .quantity(123)
                .build();
        when(productRepository.existsByNameAndIdNot(updatedproductForm.name(), 1L)).thenReturn(false);

        // when
        productService.modify(1L, updatedproductForm);

        // then
        assertThat(product1.getName()).isEqualTo(updatedproductForm.name());
        assertThat(product1.getContent()).isEqualTo(updatedproductForm.content());
        assertThat(product1.getPrice()).isEqualTo(updatedproductForm.price());
        assertThat(product1.getImgUrl()).isEqualTo(updatedproductForm.imgUrl());
        assertThat(product1.getQuantity()).isEqualTo(updatedproductForm.quantity());
    }

    @Test
    @DisplayName("중복 이름 상품 수정 테스트")
    void alreadyExistsModifyTest() {
        // given
        ProductForm updatedproductForm = ProductForm.builder()
                .name("Updated Name")
                .build();
        when(productRepository.existsByNameAndIdNot(updatedproductForm.name(), 1L)).thenReturn(true);


        // when
        ProductException exception = assertThrows(
                ProductException.class,
                () -> productService.modify(1L, updatedproductForm)
        );

        // then
        verify(productRepository, times(1)).existsByNameAndIdNot(updatedproductForm.name(), 1L);
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getCode()).isEqualTo("400-2");
        assertThat(exception.getMessage()).isEqualTo("중복된 상품 이름입니다.");
    }

    @Test
    @DisplayName("상품 삭제 성공 테스트")
    void deleteSuccessTest() {
        //given
        Long id = 1L;
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        when(productOrdersRepository.existsByProductId(id)).thenReturn(false);
        when(productRepository.findById(id)).thenReturn(Optional.of(product1));

        //when
        productService.delete(id);

        // then
        verify(productOrdersRepository, times(1)).existsByProductId(id);
        verify(productRepository, times(1)).delete(productCaptor.capture());
        Product deletedProduct = productCaptor.getValue();

        assertThat(deletedProduct.getName()).isEqualTo(name1);
        assertThat(deletedProduct.getContent()).isEqualTo(content1);
        assertThat(deletedProduct.getPrice()).isEqualTo(price1);
        assertThat(deletedProduct.getImgUrl()).isEqualTo(imgUrl1);
        assertThat(deletedProduct.getQuantity()).isEqualTo(quantity1);
    }

    @Test
    @DisplayName("상품 삭제 실패(해당 상품 존재하지 않음) 테스트")
    void deleteFailWhenProductNotExistsTest() {
        //given
        Long id = 1L;
        when(productOrdersRepository.existsByProductId(id)).thenReturn(false);
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        //when
        ProductException exception = assertThrows(ProductException.class,
                () -> productService.delete(id)
        );

        // then
        verify(productOrdersRepository, times(1)).existsByProductId(id);
        verify(productRepository, times(1)).findById(id);

        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getCode()).isEqualTo("404");
        assertThat(exception.getMessage()).isEqualTo("상품을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("상품 삭제 실패(해당 상품 주문 내역 존재) 테스트")
    void deleteFailWhenProductExistsOrderHistoryTest() {
        //given
        Long id = 1L;
        when(productOrdersRepository.existsByProductId(id)).thenReturn(true);

        //when
        ProductException exception = assertThrows(ProductException.class,
                () -> productService.delete(id)
        );

        // then
        verify(productOrdersRepository, times(1)).existsByProductId(id);

        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getCode()).isEqualTo("400-3");
        assertThat(exception.getMessage()).isEqualTo("주문 내역이 존재하는 상품입니다.");
    }

}