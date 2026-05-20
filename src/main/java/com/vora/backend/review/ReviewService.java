package com.vora.backend.review;

import com.vora.backend.product.Product;
import com.vora.backend.product.ProductRepository;
import com.vora.backend.review.dto.AddReviewRequest;
import com.vora.backend.review.dto.ProductReviewsResponse;
import com.vora.backend.review.dto.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ReviewResponse addReview(Long userId, String userName, AddReviewRequest request) {
        if (request.rating() < 1 || request.rating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        if (!request.comment().matches(".*\\S.*") || request.comment().length() < 3) {
            throw new IllegalArgumentException("Comment must be at least 3 characters long");
        }

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + request.productId()));

        Review existingReview = reviewRepository.findByProductIdAndUserId(request.productId(), userId)
                .orElse(null);

        if (existingReview != null) {
            existingReview.setRating(request.rating());
            existingReview.setComment(request.comment());
            existingReview.setUpdatedAt(LocalDateTime.now());
            existingReview = reviewRepository.save(existingReview);
        } else {
            Review review = Review.builder()
                    .product(product)
                    .userId(userId)
                    .userName(userName)
                    .rating(request.rating())
                    .comment(request.comment())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            existingReview = reviewRepository.save(review);
        }

        return toResponse(existingReview);
    }

    @Transactional(readOnly = true)
    public ProductReviewsResponse getProductReviews(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }

        List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
        Double averageRating = reviewRepository.getAverageRatingByProductId(productId);
        long totalReviews = reviewRepository.countByProductId(productId);

        List<ReviewResponse> reviewResponses = reviews.stream()
                .map(this::toResponse)
                .toList();

        return new ProductReviewsResponse(
                productId,
                averageRating != null ? averageRating : 0.0,
                totalReviews,
                reviewResponses);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getUserReviews(Long userId) {
        return reviewRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));

        if (!review.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to review");
        }

        reviewRepository.deleteById(reviewId);
    }

    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getProduct().getId(),
                review.getUserId(),
                review.getUserName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUpdatedAt());
    }
}
