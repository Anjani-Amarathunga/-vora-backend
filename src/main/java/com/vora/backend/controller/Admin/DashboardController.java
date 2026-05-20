package com.vora.backend.controller.Admin;

import com.vora.backend.product.dto.Admin_dto.response.ApiResponse;
import com.vora.backend.product.dto.Admin_dto.response.DashboardResponse;
import com.vora.backend.service.Admin_services.impl.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getSummary() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getSummary()));
    }

    @GetMapping("/sales-report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> salesReport(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getSalesReport(year, month)));
    }
}
