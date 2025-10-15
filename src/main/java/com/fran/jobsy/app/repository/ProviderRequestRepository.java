package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.dto.provider_request.ProviderRequestDTO;
import com.fran.jobsy.app.entity.ProviderRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderRequestRepository extends JpaRepository<ProviderRequest, Long> {
    @Query("""
            select new com.fran.jobsy.app.dto.provider_request.ProviderRequestDTO(
                pr.id,
                new com.fran.jobsy.app.dto.user.UserBasicDTO(
                    u.id,
                    u.firstname,
                    u.lastname,
                    u.country
                ),
                pr.bio,
                pr.hourlyRate,
                pr.yearsExperience,
                pr.addressText,
                pr.lat,
                pr.lng,
                pr.serviceRadiusKm,
                pr.rfcHomoclave,
                pr.verifiedCert,
                pr.status,
                pr.createdAt,
                pr.updatedAt,
                (
                    select 
                        cast(listagg(
                            concat(
                                pd.id, ':', pd.publicId, ':', pd.url
                            ), ','
                        ) as string)
                    from ProviderDocument pd
                    where pd.providerRequest.id = pr.id
                )
            )
            from ProviderRequest pr
            join pr.user u
            """)
    List<ProviderRequestDTO> findAllProviderRequests();
}
