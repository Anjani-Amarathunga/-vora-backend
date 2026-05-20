package com.vora.backend.user.Admin_entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "store_email", nullable = false)
    private String storeEmail;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(nullable = false, length = 64)
    private String timezone;

    @Column(nullable = false, length = 32)
    private String language;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "low_stock_threshold", nullable = false)
    private Integer lowStockThreshold;

    @Column(name = "order_prefix", nullable = false, length = 20)
    private String orderPrefix;

    @Column(name = "notify_new_orders", nullable = false)
    private Boolean notifyNewOrders;

    @Column(name = "notify_payment_updates", nullable = false)
    private Boolean notifyPaymentUpdates;

    @Column(name = "notify_low_stock_alerts", nullable = false)
    private Boolean notifyLowStockAlerts;

    @Column(name = "notify_new_users", nullable = false)
    private Boolean notifyNewUsers;

    @Column(name = "notify_system_updates", nullable = false)
    private Boolean notifySystemUpdates;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
