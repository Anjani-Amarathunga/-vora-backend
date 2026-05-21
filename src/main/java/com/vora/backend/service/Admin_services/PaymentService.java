package com.vora.backend.service.Admin_services;

import com.vora.backend.product.dto.Admin_dto.request.PaymentIntentRequest;
import com.vora.backend.product.dto.Admin_dto.response.PaymentResponse;
import java.util.List;

public interface PaymentService {
    /** Step 1 – create a Stripe PaymentIntent and persist a PENDING payment */
    PaymentResponse createPaymentIntent(PaymentIntentRequest request);

    /** Step 2 – Stripe webhook: payment_intent.succeeded */
    void handlePaymentSuccess(String paymentIntentId);

    /** Step 2b – Stripe webhook: payment_intent.payment_failed */
    void handlePaymentFailure(String paymentIntentId);

    /** Refund a payment */
    PaymentResponse refund(Long paymentId);

    PaymentResponse getByOrderId(Long orderId);
    List<PaymentResponse> getAllPayments();

    /** Validate raw Stripe webhook event signature */
    void processWebhook(String payload, String sigHeader);
}
