package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.review.ReviewRequest;
import com.fran.jobsy.app.dto.review.ReviewResponse;
import com.fran.jobsy.app.dto.review.ReviewSummaryResponse;
import com.fran.jobsy.app.service.review.ReviewService;
import com.fran.jobsy.app.util.UriBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Operaciones relacionadas con las reseñas")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/bookings/{id}/reviews")
    public ResponseEntity<ReviewResponse> createReview(@PathVariable Long id, @RequestBody @Valid ReviewRequest reviewRequest) {
        ReviewResponse review = reviewService.createReview(id, reviewRequest);
        URI location = UriBuilder.buildCreatedLocation(review.id());
        return ResponseEntity.created(location).body(review);
    }

    @GetMapping("/offerings/{id}/reviews")
    public ResponseEntity<List<ReviewSummaryResponse>> getOfferingReviews(@PathVariable Long id) {
        List<ReviewSummaryResponse> reviews = reviewService.getOfferingReviews(id);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/users/{id}/reviews")
    public ResponseEntity<List<ReviewSummaryResponse>> getProviderReviews(@PathVariable Long id) {
        List<ReviewSummaryResponse> reviews = reviewService.getProviderReviews(id);
        return ResponseEntity.ok(reviews);
    }
}
