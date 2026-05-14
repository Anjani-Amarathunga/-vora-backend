package com.vora.backend.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserId(Long userId);

    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.userId = ?1")
    void deleteByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.userId = ?1 AND c.product.id = ?2")
    void deleteByUserIdAndProductId(Long userId, Long productId);

    long countByUserId(Long userId);
}
