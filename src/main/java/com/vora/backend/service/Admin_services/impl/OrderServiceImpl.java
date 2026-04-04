package com.vora.backend.service.Admin_services.impl;

import com.vora.backend.product.dto.Admin_dto.request.OrderStatusRequest;
import com.vora.backend.product.dto.Admin_dto.response.OrderItemResponse;
import com.vora.backend.product.dto.Admin_dto.response.OrderResponse;
import com.vora.backend.user.Admin_entity.Order;
import com.vora.backend.exception.ResourceNotFoundException;
import com.vora.backend.repository.Admin_repository.OrderRepository;
import com.vora.backend.service.Admin_services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final AdminNotificationService adminNotificationService;

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        return toResponse(orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found")));
    }

    @Override
    public OrderResponse updateStatus(Long id, OrderStatusRequest req) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(req.getStatus());
        Order saved = orderRepository.save(order);

        adminNotificationService.create(
            "order",
            "Order Status Updated",
            "Order #" + saved.getId() + " changed to " + saved.getStatus().name()
        );

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getRecentOrders(int limit) {
        return orderRepository.findRecentOrders(PageRequest.of(0, limit))
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getOrderCountByStatus() {
        Map<String, Long> result = new LinkedHashMap<>();
        orderRepository.countByStatus().forEach(row ->
                result.put(row[0].toString(), (Long) row[1]));
        return result;
    }

    private OrderResponse toResponse(Order o) {
        OrderResponse r = new OrderResponse();
        r.setId(o.getId());
        r.setUserId(o.getUser().getId());
        r.setUserName(o.getUser().getName());
        r.setUserEmail(o.getUser().getEmail());
        r.setSubtotal(o.getSubtotal());
        r.setDiscountAmount(o.getDiscountAmount());
        r.setTotalAmount(o.getTotalAmount());
        r.setStatus(o.getStatus());
        r.setShippingAddress(o.getShippingAddress());
        r.setNotes(o.getNotes());
        r.setCouponCode(o.getCoupon() != null ? o.getCoupon().getCode() : null);
        r.setCreatedAt(o.getCreatedAt());
        r.setItems(o.getItems().stream().map(item -> {
            OrderItemResponse ir = new OrderItemResponse();
            ir.setId(item.getId());
            ir.setProductId(item.getProduct().getId());
            ir.setProductName(item.getProduct().getName());
            ir.setQuantity(item.getQuantity());
            ir.setUnitPrice(item.getUnitPrice());
            ir.setTotalPrice(item.getTotalPrice());
            return ir;
        }).collect(Collectors.toList()));
        return r;
    }
}
