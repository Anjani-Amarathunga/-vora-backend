package com.vora.backend.order;

import com.vora.backend.cart.CartItemRepository;
import com.vora.backend.order.dto.OrderItemResponse;
import com.vora.backend.order.dto.OrderResponse;
import com.vora.backend.order.dto.PlaceOrderRequest;
import com.vora.backend.product.Product;
import com.vora.backend.product.ProductRepository;
import com.vora.backend.cart.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponse placeOrder(Long userId, PlaceOrderRequest request) {
        if (!request.shippingAddress().matches(".*\\S.*")) {
            throw new IllegalArgumentException("Shipping address cannot be empty");
        }

        if (!request.phoneNumber().matches("^\\d{10,}$")) {
            throw new IllegalArgumentException("Phone number must be at least 10 digits");
        }

        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        BigDecimal totalAmount = cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .userId(userId)
                .orderNumber(generateOrderNumber())
                .status(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .shippingAddress(request.shippingAddress())
                .phoneNumber(request.phoneNumber())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .pricePerUnit(cartItem.getPriceAtAddTime())
                    .build();
            orderItemRepository.save(orderItem);

            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        cartItemRepository.deleteByUserId(userId);

        return toResponse(savedOrder, orderItemRepository.findByOrderId(savedOrder.getId()));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrderHistory(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(order -> toResponse(order, orderItemRepository.findByOrderId(order.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderDetails(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to order");
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return toResponse(order, orderItems);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        if (status == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }

        Order updatedOrder = orderRepository.save(order);
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return toResponse(updatedOrder, orderItems);
    }

    @Transactional(readOnly = true)
    public long getOrderCount(Long userId) {
        return orderRepository.countByUserId(userId);
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private OrderResponse toResponse(Order order, List<OrderItem> orderItems) {
        List<OrderItemResponse> itemResponses = orderItems.stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPricePerUnit(),
                        item.getSubtotal()))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getShippingAddress(),
                order.getPhoneNumber(),
                itemResponses,
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getDeliveredAt());
    }
}
