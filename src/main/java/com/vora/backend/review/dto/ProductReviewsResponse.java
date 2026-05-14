package com.vora.backend.review.dto;

import java.util.List;

public record ProductReviewsResponse(
        Long productId,
        Double averageRating,
        Long totalReviews,
        List<ReviewResponse> reviews) {
}
