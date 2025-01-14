package com.example.backend.domain.product.repository;

import com.example.backend.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ProductRepository
 * 상품 관련 Repository
 * @author 100minha
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
