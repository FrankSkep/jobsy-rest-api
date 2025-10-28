package com.fran.jobsy.app.service.offering;

import com.fran.jobsy.app.dto.offering.OfferingFilterModel;
import com.fran.jobsy.app.dto.offering.OfferingRequest;
import com.fran.jobsy.app.dto.offering.OfferingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OfferingService {
    Page<OfferingResponse> getOfferingsWithFiltersPaged(OfferingFilterModel filters, Pageable pageable);

    Page<OfferingResponse> getOfferingsPage(Pageable pageable);

    OfferingResponse createOffering(OfferingRequest offeringRequest);

    OfferingResponse getOffering(Long offeringId);

    List<OfferingResponse> getOfferingsByUserId(Long userId);

    OfferingResponse updateOffering(Long offeringId, OfferingRequest offeringRequest);
}