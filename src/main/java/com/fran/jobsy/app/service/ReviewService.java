package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.review.ReviewDTO;
import com.fran.jobsy.app.dto.review.ReviewRequest;

import java.util.List;

public interface ReviewService {
    ReviewDTO createReview(Long bookingId, ReviewRequest reviewRequest);

    List<ReviewDTO> getOfferingReviews(Long offeringId);

    List<ReviewDTO> getProviderReviews(Long providerId);
}
