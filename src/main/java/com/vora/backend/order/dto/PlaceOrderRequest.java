package com.vora.backend.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PlaceOrderRequest(
        @NotBlank(message = "Shipping address is required")
        String shippingAddress,
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[0-9]{10,}$", message = "Phone number must be at least 10 digits")
        String phoneNumber) {
}
