package com.vora.backend.cart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CartItemResponse(
        Long id,
        Long productId,
        String productName,
        BigDecimal productPrice,
        Integer quantity,
        BigDecimal priceAtAddTime,
        BigDecimal subtotal,
        LocalDateTime addedAt,
        LocalDateTime updatedAt) {
}
