package com.vora.backend.repository.Admin_repository;

import com.vora.backend.user.Admin_entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByIsActiveTrue(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           " LOWER(p.description) LIKE LOWER(CONCAT('%',:q,'%')))")
    Page<Product> search(@Param("q") String query, Pageable pageable);

    List<Product> findByCategoryIdAndIsActiveTrue(Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.stockQty <= :threshold AND p.isActive = true")
    List<Product> findLowStockProducts(@Param("threshold") int threshold);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQty = 0 AND p.isActive = true")
    long countOutOfStock();
}
