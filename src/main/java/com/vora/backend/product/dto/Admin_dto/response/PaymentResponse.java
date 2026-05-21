package com.vora.backend.product.dto.Admin_dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private Long userId;
    private String userName;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String paymentMethod;
    private String stripePaymentIntentId;
    private String clientSecret;   // only returned after intent creation
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}
