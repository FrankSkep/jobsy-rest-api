package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.entity.OfferingPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferingPhotoRepository extends JpaRepository<OfferingPhoto, Long> {

    List<OfferingPhoto> findByOfferingId(Long offeringId);

    Optional<OfferingPhoto> findByIdAndOfferingId(Long id, Long offeringId);

    @Modifying
    @Query("DELETE FROM OfferingPhoto op WHERE op.id = :id AND op.offering.id = :offeringId")
    void deleteByIdAndOfferingId(@Param("id") Long id, @Param("offeringId") Long offeringId);

    int countByOfferingId(Long offeringId);
}
