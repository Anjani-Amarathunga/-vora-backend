package com.vora.backend.controller.Admin;


import com.vora.backend.product.dto.Admin_dto.request.*;
import com.vora.backend.product.dto.Admin_dto.response.ApiResponse;
import com.vora.backend.product.dto.Admin_dto.response.OrderResponse;
import com.vora.backend.service.Admin_services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(orderService.getAllOrders(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getById(id)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusRequest req) {
        return ResponseEntity.ok(ApiResponse.success(orderService.updateStatus(id, req)));
    }

    @GetMapping("/status-summary")
    public ResponseEntity<ApiResponse<Map<String, Long>>> statusSummary() {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderCountByStatus()));
    }
}
