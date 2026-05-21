package com.vora.backend.controller.Admin;

import com.vora.backend.product.dto.Admin_dto.request.PaymentIntentRequest;
import com.vora.backend.product.dto.Admin_dto.response.ApiResponse;
import com.vora.backend.product.dto.Admin_dto.response.PaymentResponse;
import com.vora.backend.service.Admin_services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /** Called by the user frontend after order is placed */
    @PostMapping("/create-intent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaymentResponse>> createIntent(
            @Valid @RequestBody PaymentIntentRequest req) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.createPaymentIntent(req)));
    }

    /** Admin: list all payments */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getAllPayments()));
    }

    /** Admin: get payment by order */
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getByOrderId(orderId)));
    }

    /** Admin: issue refund */
    @PostMapping("/{paymentId}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> refund(@PathVariable Long paymentId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.refund(paymentId)));
    }

    /**
     * Stripe Webhook — MUST be public (no auth).
     * Register this URL in the Stripe dashboard:
     *   https://yourdomain.com/api/payments/webhook
     */
    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> stripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        paymentService.processWebhook(payload, sigHeader);
        return ResponseEntity.ok("OK");
    }
}
