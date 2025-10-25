package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.review.ReviewDTO;
import com.fran.jobsy.app.dto.review.ReviewRequest;
import com.fran.jobsy.app.dto.review.ReviewSummaryDTO;
import com.fran.jobsy.app.service.ReviewService;
import com.fran.jobsy.app.util.RestUtils;
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
    public ResponseEntity<ReviewDTO> createReview(@PathVariable Long id, @RequestBody @Valid ReviewRequest reviewRequest) {
        ReviewDTO review = reviewService.createReview(id, reviewRequest);
        URI location = RestUtils.buildCreatedLocation(review.id());
        return ResponseEntity.created(location).body(review);
    }

    @GetMapping("/offerings/{id}/reviews")
    public ResponseEntity<List<ReviewSummaryDTO>> getOfferingReviews(@PathVariable Long id) {
        List<ReviewSummaryDTO> reviews = reviewService.getOfferingReviews(id);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/users/{id}/reviews")
    public ResponseEntity<List<ReviewSummaryDTO>> getProviderReviews(@PathVariable Long id) {
        List<ReviewSummaryDTO> reviews = reviewService.getProviderReviews(id);
        return ResponseEntity.ok(reviews);
    }
}
