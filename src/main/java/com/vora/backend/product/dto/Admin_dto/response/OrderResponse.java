package com.vora.backend.product.dto.Admin_dto.response;

import com.vora.backend.enums.OrderStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private List<OrderItemResponse> items;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String shippingAddress;
    private String notes;
    private String couponCode;
    private PaymentResponse payment;
    private LocalDateTime createdAt;
}
