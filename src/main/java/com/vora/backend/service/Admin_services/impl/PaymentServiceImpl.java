package com.vora.backend.service.Admin_services.impl;

import com.vora.backend.product.dto.Admin_dto.request.PaymentIntentRequest;
import com.vora.backend.product.dto.Admin_dto.response.PaymentResponse;
import com.vora.backend.user.Admin_entity.*;
import com.vora.backend.enums.OrderStatus;
import com.vora.backend.enums.PaymentStatus;
import com.vora.backend.enums.TransactionType;
import com.vora.backend.exception.BadRequestException;
import com.vora.backend.exception.ResourceNotFoundException;
import com.vora.backend.repository.Admin_repository.*;
import com.vora.backend.service.Admin_services.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AdminNotificationService adminNotificationService;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    // ── Step 1: create PaymentIntent ─────────────────────────────────────────
    @Override
    public PaymentResponse createPaymentIntent(PaymentIntentRequest req) {
        Order order = orderRepository.findById(req.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Prevent duplicate payment
        if (paymentRepository.findByOrderId(order.getId()).isPresent()) {
            throw new BadRequestException("Payment already initiated for this order");
        }

        try {
            // Amount in cents (Stripe requires smallest currency unit)
            long amountInCents = order.getTotalAmount()
                    .multiply(BigDecimal.valueOf(100)).longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(req.getCurrency() != null ? req.getCurrency() : "usd")
                    .setDescription("Order #" + order.getId())
                    .putMetadata("order_id", order.getId().toString())
                    .putMetadata("user_id", order.getUser().getId().toString())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true).build())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            // Persist payment record
            Payment payment = Payment.builder()
                    .order(order)
                    .user(order.getUser())
                    .amount(order.getTotalAmount())
                    .currency(req.getCurrency() != null ? req.getCurrency() : "USD")
                    .status(PaymentStatus.PENDING)
                    .stripePaymentIntentId(intent.getId())
                    .build();

            paymentRepository.save(payment);

            PaymentResponse response = toResponse(payment);
            response.setClientSecret(intent.getClientSecret()); // returned to frontend
            return response;

        } catch (StripeException e) {
            log.error("Stripe error creating PaymentIntent: {}", e.getMessage());
            throw new RuntimeException("Payment initiation failed: " + e.getMessage());
        }
    }

    // ── Step 2a: webhook success ──────────────────────────────────────────────
    @Override
    public void handlePaymentSuccess(String paymentIntentId) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for intent: " + paymentIntentId));

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // Update order status
        Order order = payment.getOrder();
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        // Record transaction
        Transaction txn = Transaction.builder()
                .payment(payment)
                .type(TransactionType.CHARGE)
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .stripeTxnId(paymentIntentId)
                .status(PaymentStatus.SUCCESS)
                .description("Payment successful for Order #" + order.getId())
                .build();
        transactionRepository.save(txn);

        log.info("Payment SUCCESS for order #{}", order.getId());

        adminNotificationService.create(
            "payment",
            "Payment Successful",
            "Payment confirmed for Order #" + order.getId()
        );
    }

    // ── Step 2b: webhook failure ──────────────────────────────────────────────
    @Override
    public void handlePaymentFailure(String paymentIntentId) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for intent: " + paymentIntentId));

        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);

        // Record failed transaction
        Transaction txn = Transaction.builder()
                .payment(payment)
                .type(TransactionType.CHARGE)
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .stripeTxnId(paymentIntentId)
                .status(PaymentStatus.FAILED)
                .description("Payment FAILED for Order #" + payment.getOrder().getId())
                .build();
        transactionRepository.save(txn);

        log.warn("Payment FAILED for order #{}", payment.getOrder().getId());

        adminNotificationService.create(
            "payment",
            "Payment Failed",
            "Payment failed for Order #" + payment.getOrder().getId()
        );
    }

    // ── Refund ────────────────────────────────────────────────────────────────
    @Override
    public PaymentResponse refund(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new BadRequestException("Only successful payments can be refunded");
        }

        try {
            long amountInCents = payment.getAmount()
                    .multiply(BigDecimal.valueOf(100)).longValue();

            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(payment.getStripePaymentIntentId())
                    .setAmount(amountInCents)
                    .build();

            Refund refund = Refund.create(params);

            payment.setStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(payment);

            // Update order
            payment.getOrder().setStatus(OrderStatus.REFUNDED);
            orderRepository.save(payment.getOrder());

            // Record transaction
            Transaction txn = Transaction.builder()
                    .payment(payment)
                    .type(TransactionType.REFUND)
                    .amount(payment.getAmount())
                    .currency(payment.getCurrency())
                    .stripeTxnId(refund.getId())
                    .status(PaymentStatus.SUCCESS)
                    .description("Refund issued for Order #" + payment.getOrder().getId())
                    .build();
            transactionRepository.save(txn);

            log.info("Refund issued for order #{}", payment.getOrder().getId());

                adminNotificationService.create(
                    "payment",
                    "Refund Processed",
                    "Refund issued for Order #" + payment.getOrder().getId()
                );

            return toResponse(payment);

        } catch (StripeException e) {
            log.error("Stripe refund error: {}", e.getMessage());
            throw new RuntimeException("Refund failed: " + e.getMessage());
        }
    }

    // ── Webhook processor ─────────────────────────────────────────────────────
    @Override
    public void processWebhook(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new BadRequestException("Invalid Stripe webhook signature");
        }

        log.info("Stripe webhook received: {}", event.getType());

        switch (event.getType()) {
            case "payment_intent.succeeded" -> {
                PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject().orElseThrow();
                handlePaymentSuccess(intent.getId());
            }
            case "payment_intent.payment_failed" -> {
                PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject().orElseThrow();
                handlePaymentFailure(intent.getId());
            }
            default -> log.info("Unhandled event type: {}", event.getType());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getByOrderId(Long orderId) {
        return toResponse(paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    private PaymentResponse toResponse(Payment p) {
        PaymentResponse r = new PaymentResponse();
        r.setId(p.getId());
        r.setOrderId(p.getOrder().getId());
        r.setUserId(p.getUser().getId());
        r.setUserName(p.getUser().getName());
        r.setAmount(p.getAmount());
        r.setCurrency(p.getCurrency());
        r.setStatus(p.getStatus().name());
        r.setPaymentMethod(p.getPaymentMethod());
        r.setStripePaymentIntentId(p.getStripePaymentIntentId());
        r.setPaidAt(p.getPaidAt());
        r.setCreatedAt(p.getCreatedAt());
        return r;
    }
}
