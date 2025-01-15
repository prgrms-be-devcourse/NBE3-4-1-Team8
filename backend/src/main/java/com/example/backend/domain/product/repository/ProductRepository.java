package com.example.backend.domain.product.repository;

import com.example.backend.domain.product.dto.ProductResponse;
import com.example.backend.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ProductRepository
 * 상품 관련 Repository
 * @author 100minha
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT new com.example.backend.domain.product.dto.ProductResponse(p.id, p.name, p.content, " +
            "p.price, p.imgUrl, p.quantity) " +
            "FROM Product p " +
            "WHERE p.id = :id")
    Optional<ProductResponse> findProductResponseById(@Param("id") Long id);
}
