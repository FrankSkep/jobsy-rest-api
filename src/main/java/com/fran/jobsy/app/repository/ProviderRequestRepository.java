package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.entity.ProviderRequest;
import com.fran.jobsy.app.enums.ProviderRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderRequestRepository extends JpaRepository<ProviderRequest, Long> {

    boolean existsByUserIdAndStatus(Long userId, ProviderRequestStatus status);

    Page<ProviderRequest> findAll(Pageable pageable);

//    @Query("""
//            select new com.fran.jobsy.app.dto.provider_request.ProviderRequestDTO(
//                pr.id,
//                new com.fran.jobsy.app.dto.user.UserBasicDTO(
//                    u.id,
//                    u.firstname,
//                    u.lastname,
//                    u.country
//                ),
//                pr.bio,
//                pr.hourlyRate,
//                pr.yearsExperience,
//                pr.addressText,
//                pr.lat,
//                pr.lng,
//                pr.serviceRadiusKm,
//                pr.rfcHomoclave,
//                pr.verifiedCert,
//                pr.status,
//                pr.createdAt,
//                pr.updatedAt,
//                (
//                    select
//                        cast(listagg(
//                            concat(
//                                pd.id, ':', pd.publicId, ':', pd.url
//                            ), ','
//                        ) as string)
//                    from ProviderDocument pd
//                    where pd.providerRequest.id = pr.id
//                )
//            )
//            from ProviderRequest pr
//            join pr.user u
//            """)
//    List<ProviderRequestDTO> findAllProviderRequests();
}
