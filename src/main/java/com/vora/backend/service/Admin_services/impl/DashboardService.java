package com.vora.backend.service.Admin_services.impl;

import com.vora.backend.product.dto.Admin_dto.response.DashboardResponse;
import com.vora.backend.enums.OrderStatus;
import com.vora.backend.repository.Admin_repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public DashboardResponse getSummary() {
        DashboardResponse d = new DashboardResponse();
        d.setTotalUsers(userRepository.countActiveUsers());
        d.setTotalOrders(orderRepository.countActiveOrders(OrderStatus.CANCELLED));
        d.setTotalRevenue(orderRepository.totalRevenue(OrderStatus.DELIVERED));
        d.setOutOfStockProducts(productRepository.countOutOfStock());

        // Monthly revenue for current year
        int year = LocalDate.now().getYear();
        List<Object[]> monthlyData = orderRepository.monthlyRevenue(year, OrderStatus.DELIVERED);
        Map<Integer, Double> monthly = new LinkedHashMap<>();
        for (int m = 1; m <= 12; m++) monthly.put(m, 0.0);
        monthlyData.forEach(row ->
                monthly.put(((Number) row[0]).intValue(), ((Number) row[1]).doubleValue()));
        d.setMonthlyRevenue(monthly);

        // Order status breakdown
        d.setOrderStatusBreakdown(orderRepository.countByStatus()
                .stream().collect(java.util.stream.Collectors.toMap(
                        r -> r[0].toString(), r -> ((Number) r[1]).longValue())));

        // Recent orders
        d.setRecentOrders(orderRepository.findRecentOrders(
                org.springframework.data.domain.PageRequest.of(0, 5))
                .stream().map(o -> {
                    DashboardResponse.RecentOrder ro = new DashboardResponse.RecentOrder();
                    ro.setOrderId(o.getId());
                    ro.setCustomerName(o.getUser().getName());
                    ro.setAmount(o.getTotalAmount());
                    ro.setStatus(o.getStatus().name());
                    ro.setCreatedAt(o.getCreatedAt());
                    return ro;
                }).toList());

        return d;
    }

    public Map<String, Object> getSalesReport(int year, int month) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("year", year);
        report.put("month", month);
        report.put("revenue", orderRepository.revenueByDateRange(start, end, OrderStatus.DELIVERED));
        report.put("orders", orderRepository.findAll().stream()
                .filter(o -> !o.getCreatedAt().isBefore(start) && !o.getCreatedAt().isAfter(end))
                .count());
        return report;
    }
}
