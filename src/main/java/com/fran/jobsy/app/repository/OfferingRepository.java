package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.dto.offering.OfferingDTO;
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

    @Query("SELECT new com.fran.jobsy.app.dto.offering.OfferingDTO(s.id, " +
            "new com.fran.jobsy.app.dto.user.UserServiceDTO(s.owner.id, s.owner.lastname, s.owner.firstname), " +
            "s.category.name, s.title, s.description, s.basePrice) FROM Offering s")
    List<OfferingDTO> findAllServices();

    @Query("SELECT new com.fran.jobsy.app.dto.offering.OfferingDTO(s.id, " +
            "new com.fran.jobsy.app.dto.user.UserServiceDTO(s.owner.id, s.owner.lastname, s.owner.firstname), " +
            "s.category.name, s.title, s.description, s.basePrice) FROM Offering s")
    Page<OfferingDTO> findAllServicesPaged(Pageable pageable);

    @Query("SELECT new com.fran.jobsy.app.dto.offering.OfferingDTO(s.id, " +
            "new com.fran.jobsy.app.dto.user.UserServiceDTO(s.owner.id, s.owner.lastname, s.owner.firstname), " +
            "s.category.name, s.title, s.description, s.basePrice) FROM Offering s " +
            "WHERE (:categoryId IS NULL OR s.category.id = :categoryId) " +
            "AND (:minPrice IS NULL OR s.basePrice >= :minPrice) " +
            "AND (:maxPrice IS NULL OR s.basePrice <= :maxPrice) " +
            "AND (:minRating IS NULL OR s.owner.avgRatingCache >= :minRating) " +
            "AND (:location IS NULL OR LOWER(s.owner.addressText) LIKE LOWER(CONCAT('%', :location, '%')))")
    Page<OfferingDTO> findServicesWithFiltersPaged(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("minRating") Double minRating,
            @Param("location") String location,
            Pageable pageable
    );

    @Query("SELECT new com.fran.jobsy.app.dto.offering.OfferingDTO(s.id, " +
            "new com.fran.jobsy.app.dto.user.UserServiceDTO(s.owner.id, s.owner.lastname, s.owner.firstname), " +
            "s.category.name, s.title, s.description, s.basePrice) FROM Offering s " +
            "WHERE (:categoryId IS NULL OR s.category.id = :categoryId) " +
            "AND (:minPrice IS NULL OR s.basePrice >= :minPrice) " +
            "AND (:maxPrice IS NULL OR s.basePrice <= :maxPrice) " +
            "AND (:minRating IS NULL OR s.owner.avgRatingCache >= :minRating) " +
            "AND (:location IS NULL OR LOWER(s.owner.addressText) LIKE LOWER(CONCAT('%', :location, '%')))")
    List<OfferingDTO> findServicesWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("minRating") Double minRating,
            @Param("location") String location
    );

    @Query("SELECT new com.fran.jobsy.app.dto.offering.OfferingDTO(s.id, " +
            "new com.fran.jobsy.app.dto.user.UserServiceDTO(s.owner.id, s.owner.lastname, s.owner.firstname), " +
            "s.category.name, s.title, s.description, s.basePrice) FROM Offering s " +
            "WHERE s.owner.id = :ownerId")
    List<OfferingDTO> findByOwnerId(@Param("ownerId") Long ownerId);

}