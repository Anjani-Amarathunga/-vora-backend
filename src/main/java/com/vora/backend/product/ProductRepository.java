package com.vora.backend.product;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    java.util.List<Product> findByNameContainingIgnoreCase(String name);

    java.util.List<Product> findByPriceBetween(java.math.BigDecimal min, java.math.BigDecimal max);
}
