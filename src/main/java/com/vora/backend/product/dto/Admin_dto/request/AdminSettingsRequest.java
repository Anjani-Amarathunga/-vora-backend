package com.vora.backend.product.dto.Admin_dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AdminSettingsRequest {
    @NotBlank
    private String storeName;

    @NotBlank
    @Email
    private String storeEmail;

    @NotBlank
    private String currency;

    @NotBlank
    private String timezone;

    @NotBlank
    private String language;

    private String address;

    @NotNull
    @Min(1)
    private Integer lowStockThreshold;

    @NotBlank
    private String orderPrefix;

    @NotNull
    private Boolean notifyNewOrders;

    @NotNull
    private Boolean notifyPaymentUpdates;

    @NotNull
    private Boolean notifyLowStockAlerts;

    @NotNull
    private Boolean notifyNewUsers;

    @NotNull
    private Boolean notifySystemUpdates;
}
