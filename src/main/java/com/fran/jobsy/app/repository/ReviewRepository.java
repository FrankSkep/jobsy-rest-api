// language: java
package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.dto.review.ReviewSummaryResponse;
import com.fran.jobsy.app.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("""
            SELECT new com.fran.jobsy.app.dto.review.ReviewSummaryResponse(
                r.id,
                r.rating,
                r.comment,
                CONCAT(b.client.firstname, ' ', b.client.lastname),
                b.offering.title,
                r.createdAt
            )
            FROM Review r
            JOIN r.booking b
            WHERE b.offering.id = :offeringId
            """)
    List<ReviewSummaryResponse> findByBooking_OfferingId(@Param("offeringId") Long offeringId);

    @Query("""
            SELECT new com.fran.jobsy.app.dto.review.ReviewSummaryResponse(
                r.id,
                r.rating,
                r.comment,
                CONCAT(b.client.firstname, ' ', b.client.lastname),
                b.offering.title,
                r.createdAt
            )
            FROM Review r
            JOIN r.booking b
            WHERE r.provider.id = :providerId
            """)
    List<ReviewSummaryResponse> findByProviderId(@Param("providerId") Long providerId);
}