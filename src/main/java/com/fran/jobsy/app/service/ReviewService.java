package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.review.ReviewRequest;
import com.fran.jobsy.app.dto.review.ReviewResponse;
import com.fran.jobsy.app.dto.review.ReviewSummaryResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(Long bookingId, ReviewRequest reviewRequest);

    List<ReviewSummaryResponse> getOfferingReviews(Long offeringId);

    List<ReviewSummaryResponse> getProviderReviews(Long providerId);
}
