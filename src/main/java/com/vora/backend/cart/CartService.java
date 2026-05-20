package com.vora.backend.cart;

import com.vora.backend.cart.dto.AddToCartRequest;
import com.vora.backend.cart.dto.CartItemResponse;
import com.vora.backend.cart.dto.CartResponse;
import com.vora.backend.cart.dto.UpdateCartItemRequest;
import com.vora.backend.product.Product;
import com.vora.backend.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CartItemResponse addToCart(Long userId, AddToCartRequest request) {
        if (request.quantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + request.productId()));

        if (product.getStock() < request.quantity()) {
            throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
        }

        CartItem existing = cartItemRepository.findByUserIdAndProductId(userId, request.productId())
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.quantity());
            existing.setUpdatedAt(LocalDateTime.now());
            existing = cartItemRepository.save(existing);
        } else {
            CartItem cartItem = CartItem.builder()
                    .userId(userId)
                    .product(product)
                    .quantity(request.quantity())
                    .priceAtAddTime(product.getPrice())
                    .addedAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            existing = cartItemRepository.save(cartItem);
        }

        return toResponse(existing);
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        List<CartItemResponse> itemResponses = items.stream()
                .map(this::toResponse)
                .toList();

        BigDecimal totalPrice = items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(itemResponses, items.size(), totalPrice);
    }

    @Transactional
    public CartItemResponse updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequest request) {
        if (request.quantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found: " + cartItemId));

        if (!cartItem.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to cart item");
        }

        if (cartItem.getProduct().getStock() < request.quantity()) {
            throw new IllegalArgumentException("Insufficient stock for product: " + cartItem.getProduct().getName());
        }

        cartItem.setQuantity(request.quantity());
        cartItem.setUpdatedAt(LocalDateTime.now());
        cartItem = cartItemRepository.save(cartItem);

        return toResponse(cartItem);
    }

    @Transactional
    public void removeFromCart(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found: " + cartItemId));

        if (!cartItem.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to cart item");
        }

        cartItemRepository.deleteById(cartItemId);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    private CartItemResponse toResponse(CartItem cartItem) {
        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getProduct().getId(),
                cartItem.getProduct().getName(),
                cartItem.getProduct().getPrice(),
                cartItem.getQuantity(),
                cartItem.getPriceAtAddTime(),
                cartItem.getSubtotal(),
                cartItem.getAddedAt(),
                cartItem.getUpdatedAt());
    }
}
