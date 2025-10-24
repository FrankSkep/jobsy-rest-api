package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.review.ReviewDTO;
import com.fran.jobsy.app.dto.review.ReviewRequest;
import com.fran.jobsy.app.dto.review.ReviewSummaryDTO;

import java.util.List;

public interface ReviewService {
    ReviewDTO createReview(Long bookingId, ReviewRequest reviewRequest);

    List<ReviewSummaryDTO> getOfferingReviews(Long offeringId);

    List<ReviewSummaryDTO> getProviderReviews(Long providerId);
}
