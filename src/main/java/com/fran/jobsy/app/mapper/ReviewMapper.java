package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.review.ReviewResponse;
import com.fran.jobsy.app.dto.review.ReviewSummaryResponse;
import com.fran.jobsy.app.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BookingMapper.class})
public interface ReviewMapper {
    ReviewResponse toReviewDTO(Review review);

    @Mapping(target = "clientName", expression = "java(mapClientName(review))")
    @Mapping(target = "offeringTitle", source = "booking.offering.title")
    ReviewSummaryResponse toReviewSummaryDTO(Review review);

    default String mapClientName(Review review) {
        return review.getBooking().getClient().getFirstname() + " " + review.getBooking().getClient().getLastname();
    }
}
