package com.vora.backend.product.dto.Admin_dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSettingsResponse {
    private Long id;
    private String storeName;
    private String storeEmail;
    private String currency;
    private String timezone;
    private String language;
    private String address;
    private Integer lowStockThreshold;
    private String orderPrefix;
    private Boolean notifyNewOrders;
    private Boolean notifyPaymentUpdates;
    private Boolean notifyLowStockAlerts;
    private Boolean notifyNewUsers;
    private Boolean notifySystemUpdates;
    private LocalDateTime updatedAt;
}
