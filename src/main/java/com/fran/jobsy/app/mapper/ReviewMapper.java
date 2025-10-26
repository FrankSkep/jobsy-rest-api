package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.review.ReviewDTO;
import com.fran.jobsy.app.dto.review.ReviewSummaryDTO;
import com.fran.jobsy.app.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BookingMapper.class})
public interface ReviewMapper {
    ReviewDTO toReviewDTO(Review review);

    @Mapping(target = "clientName", expression = "java(mapClientName(review)))")
    @Mapping(target = "offeringTitle", source = "booking.offering.title")
    ReviewSummaryDTO toReviewSummaryDTO(Review review);

    default String mapClientName(Review review) {
        return review.getBooking().getClient().getFirstname() + " " + review.getBooking().getClient().getLastname();
    }
}
