package com.vora.backend.controller.Admin;


import com.vora.backend.product.dto.Admin_dto.request.AdminSettingsRequest;
import com.vora.backend.product.dto.Admin_dto.response.AdminSettingsResponse;
import com.vora.backend.product.dto.Admin_dto.response.ApiResponse;
//import com.vora.backend.service.Admin_services.impl.AdminSettingsService;
import com.vora.backend.service.Admin_services.impl.AdminSettingsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/settings")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminSettingsController {

    private final AdminSettingsService adminSettingsService;

    @GetMapping
    public ResponseEntity<ApiResponse<AdminSettingsResponse>> getSettings() {
        return ResponseEntity.ok(ApiResponse.success(adminSettingsService.getSettings()));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<AdminSettingsResponse>> updateSettings(
            @Valid @RequestBody AdminSettingsRequest req) {
        return ResponseEntity.ok(ApiResponse.success(adminSettingsService.updateSettings(req)));
    }
}
