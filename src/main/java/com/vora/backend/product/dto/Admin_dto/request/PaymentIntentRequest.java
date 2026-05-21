package com.vora.backend.product.dto.Admin_dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class PaymentIntentRequest {
    @NotNull private Long orderId;
    private String currency = "USD";
}
