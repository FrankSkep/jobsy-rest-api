package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.entity.Offering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferingRepository extends JpaRepository<Offering, Long> {

    @Query("SELECT DISTINCT s FROM Offering s " +
            "WHERE s.isActive = true")
    Page<Offering> findAllServicesPaged(Pageable pageable);

    @Query("SELECT DISTINCT s FROM Offering s " +
            "LEFT JOIN Booking b ON b.offering.id = s.id " +
            "LEFT JOIN Review r ON r.booking.id = b.id " +
            "WHERE s.isActive = true " +
            "AND (:categoryId IS NULL OR s.category.id = :categoryId) " +
            "AND (:minPrice IS NULL OR s.basePrice >= :minPrice) " +
            "AND (:maxPrice IS NULL OR s.basePrice <= :maxPrice) " +
            "AND (:location IS NULL OR LOWER(s.owner.addressText) LIKE LOWER(CONCAT('%', :location, '%'))) " +
            "AND (:title IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "GROUP BY s.id " +
            "HAVING (:minRating IS NULL OR AVG(r.rating) >= :minRating OR AVG(r.rating) IS NULL)")
    Page<Offering> findServicesWithFiltersPaged(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("minRating") Double minRating,
            @Param("location") String location,
            @Param("title") String title,
            Pageable pageable
    );

    List<Offering> findByOwnerIdAndIsActive(Long ownerId, boolean isActive);

    List<Offering> findByOwnerId(Long ownerId);

    @Query("""
            SELECT AVG(r.rating) 
            FROM Review r 
            JOIN r.booking b 
            WHERE b.offering.id = :offeringId
            """)
    Double findAverageRatingByOfferingId(@Param("offeringId") Long offeringId);

    @Query("""
            SELECT COUNT(r) 
            FROM Review r 
            JOIN r.booking b 
            WHERE b.offering.id = :offeringId
            """)
    Integer countReviewsByOfferingId(@Param("offeringId") Long offeringId);

}