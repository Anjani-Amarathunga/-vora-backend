package com.vora.backend.controller.Admin;


import com.vora.backend.product.dto.Admin_dto.response.AdminNotificationResponse;
import com.vora.backend.product.dto.Admin_dto.response.ApiResponse;
import com.vora.backend.service.Admin_services.impl.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/notifications")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final AdminNotificationService adminNotificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminNotificationResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(adminNotificationService.getAll()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> unreadCount() {
        return ResponseEntity.ok(ApiResponse.success(Map.of("unreadCount", adminNotificationService.getUnreadCount())));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<AdminNotificationResponse>> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(adminNotificationService.markAsRead(id)));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        adminNotificationService.markAllAsRead();
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        adminNotificationService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearAll() {
        adminNotificationService.clearAll();
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
