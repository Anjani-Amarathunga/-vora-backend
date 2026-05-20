package com.vora.backend.order.dto;

import com.vora.backend.order.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        OrderStatus status,
        BigDecimal totalAmount,
        String shippingAddress,
        String phoneNumber,
        List<OrderItemResponse> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deliveredAt) {
}
