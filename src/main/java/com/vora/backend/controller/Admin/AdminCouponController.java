package com.vora.backend.controller.Admin;


import com.vora.backend.product.dto.Admin_dto.request.CouponRequest;
import com.vora.backend.product.dto.Admin_dto.response.ApiResponse;
import com.vora.backend.product.dto.Admin_dto.response.CategoryResponse;
import com.vora.backend.product.dto.Admin_dto.response.CouponResponse;
import com.vora.backend.service.Admin_services.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin/coupons")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponService couponService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(couponService.getAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CouponResponse>> create(
            @Valid @RequestBody CouponRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(couponService.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CouponRequest req) {
        return ResponseEntity.ok(ApiResponse.success(couponService.update(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        couponService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

// ── Public endpoint for users to validate coupons ──────────────────────────
// Add this to a separate PublicCouponController or expose via user API:
//  POST /api/coupons/validate  { code, orderAmount }  → discountAmount
