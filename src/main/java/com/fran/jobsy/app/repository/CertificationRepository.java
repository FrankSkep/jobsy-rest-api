package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.dto.CertificationResponse;
import com.fran.jobsy.app.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {
    boolean existsByUserIdAndNameIgnoreCaseAndIssuerIgnoreCaseAndYear(Long userId, String name, String issuer, Integer year);

    Optional<Certification> findByIdAndUserId(Long certificationId, Long userId);

    @Query("SELECT NEW com.fran.jobsy.app.dto.CertificationResponse(" +
            "c.id, c.name, c.issuer, c.year ) FROM Certification c WHERE c.user.id = :userId")
    List<CertificationResponse> findAllByUserId(@Param("userId") Long userId);
}
