package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.entity.OfferingPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferingPhotoRepository extends JpaRepository<OfferingPhoto, Long> {

    List<OfferingPhoto> findByOfferingId(Long offeringId);

    Optional<OfferingPhoto> findByIdAndOfferingId(Long id, Long offeringId);

    void deleteByIdAndOfferingId(Long id, Long offeringId);
}
