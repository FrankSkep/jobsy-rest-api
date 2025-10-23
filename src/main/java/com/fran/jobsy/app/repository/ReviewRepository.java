// language: java
package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.dto.review.ReviewDTO;
import com.fran.jobsy.app.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("""
            SELECT new com.fran.jobsy.app.dto.review.ReviewDTO(
                r.id,
                new com.fran.jobsy.app.dto.booking.BookingListDTO(
                    b.id,
                    b.provider.firstname,
                    b.client.firstname,
                    b.offering.title,
                    b.startsAt,
                    b.endsAt,
                    b.status,
                    b.priceAtBooking
                ),
                r.client.id,
                r.provider.id,
                r.rating,
                r.comment,
                r.createdAt
            )
            FROM Review r
            JOIN r.booking b
            WHERE b.offering.id = :offeringId
            """)
    List<ReviewDTO> findByBooking_OfferingId(@Param("offeringId") Long offeringId);

    @Query("""
            SELECT new com.fran.jobsy.app.dto.review.ReviewDTO(
                r.id,
                new com.fran.jobsy.app.dto.booking.BookingListDTO(
                    b.id,
                    b.provider.firstname,
                    b.client.firstname,
                    b.offering.title,
                    b.startsAt,
                    b.endsAt,
                    b.status,
                    b.priceAtBooking
                ),
                r.client.id,
                r.provider.id,
                r.rating,
                r.comment,
                r.createdAt
            )
            FROM Review r
            JOIN r.booking b
            WHERE r.provider.id = :providerId
            """)
    List<ReviewDTO> findByProviderId(@Param("providerId") Long providerId);
}