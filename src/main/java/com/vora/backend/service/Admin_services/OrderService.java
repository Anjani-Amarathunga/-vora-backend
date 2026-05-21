package com.vora.backend.service.Admin_services;

import com.vora.backend.product.dto.Admin_dto.request.OrderStatusRequest;
import com.vora.backend.product.dto.Admin_dto.response.OrderResponse;
import com.vora.backend.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface OrderService {
    Page<OrderResponse> getAllOrders(Pageable pageable);
    OrderResponse getById(Long id);
    OrderResponse updateStatus(Long id, OrderStatusRequest request);
    List<OrderResponse> getRecentOrders(int limit);
    Map<String, Long> getOrderCountByStatus();
}
