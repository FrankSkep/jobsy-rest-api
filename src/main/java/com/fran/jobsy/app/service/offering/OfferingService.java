package com.fran.jobsy.app.service.offering;

import com.fran.jobsy.app.dto.offering.OfferingFilterModel;
import com.fran.jobsy.app.dto.offering.OfferingMinimalResponse;
import com.fran.jobsy.app.dto.offering.OfferingRequest;
import com.fran.jobsy.app.dto.offering.OfferingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OfferingService {
    Page<OfferingMinimalResponse> getOfferingsWithFiltersPaged(OfferingFilterModel filters, Pageable pageable);

    Page<OfferingMinimalResponse> getOfferingsPage(Pageable pageable);

    List<OfferingMinimalResponse> getUserOfferings(Long userId);

    List<OfferingMinimalResponse> getMyOfferings();

    OfferingResponse createOffering(OfferingRequest offeringRequest);

    OfferingResponse getOffering(Long offeringId);

    OfferingResponse updateOffering(Long offeringId, OfferingRequest offeringRequest);

    void deleteOffering(Long offeringId);

    OfferingResponse toggleOfferingStatus(Long offeringId);
}