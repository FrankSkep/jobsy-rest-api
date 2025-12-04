package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.dto.dashboard.PendingProviderRequestDTO;
import com.fran.jobsy.app.entity.ProviderRequest;
import com.fran.jobsy.app.enums.ProviderRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRequestRepository extends JpaRepository<ProviderRequest, Long> {

    boolean existsByUserIdAndStatus(Long userId, ProviderRequestStatus status);

    Page<ProviderRequest> findAll(Pageable pageable);

    Optional<ProviderRequest> findByUserId(Long userId);

    List<ProviderRequest> findAllByUserId(Long userId);

    // Dashboard statistics
    long countByStatus(ProviderRequestStatus status);

    @Query("""
            SELECT new com.fran.jobsy.app.dto.dashboard.PendingProviderRequestDTO(
                pr.id,
                pr.user.id,
                CONCAT(pr.user.firstname, ' ', pr.user.lastname),
                CAST(pr.status AS string),
                pr.createdAt,
                pr.bio
            )
            FROM ProviderRequest pr
            WHERE pr.status = :status
            ORDER BY pr.createdAt DESC
            """)
    List<PendingProviderRequestDTO> findByStatusAsDTO(@Param("status") ProviderRequestStatus status, Pageable pageable);
}
