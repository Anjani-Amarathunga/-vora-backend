package com.vora.backend.review;

import com.vora.backend.review.dto.AddReviewRequest;
import com.vora.backend.review.dto.ProductReviewsResponse;
import com.vora.backend.review.dto.ReviewResponse;
import com.vora.backend.user.Admin_entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> addReview(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AddReviewRequest request) {
        ReviewResponse response = reviewService.addReview(user.getId(), user.getFullName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductReviewsResponse> getProductReviews(@PathVariable Long productId) {
        ProductReviewsResponse response = reviewService.getProductReviews(productId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<List<ReviewResponse>> getUserReviews(@AuthenticationPrincipal User user) {
        List<ReviewResponse> reviews = reviewService.getUserReviews(user.getId());
        return ResponseEntity.ok(reviews);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal User user,
            @PathVariable Long reviewId) {
        reviewService.deleteReview(user.getId(), reviewId);
        return ResponseEntity.noContent().build();
    }
}
