package com.vora.backend.product.dto.Admin_dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class DashboardResponse {
    private long totalUsers;
    private long totalOrders;
    private BigDecimal totalRevenue;
    private long outOfStockProducts;
    private Map<Integer, Double> monthlyRevenue;          // month 1-12 -> revenue
    private Map<String, Long> orderStatusBreakdown;
    private List<RecentOrder> recentOrders;

    @Data
    public static class RecentOrder {
        private Long orderId;
        private String customerName;
        private BigDecimal amount;
        private String status;
        private LocalDateTime createdAt;
    }
}
