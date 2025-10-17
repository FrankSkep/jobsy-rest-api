package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.offering.OfferingDTO;
import com.fran.jobsy.app.dto.offering.OfferingFilterDTO;
import com.fran.jobsy.app.dto.offering.OfferingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OfferingService {
    Page<OfferingDTO> getOfferingsWithFiltersPaged(OfferingFilterDTO filters, Pageable pageable);

    Page<OfferingDTO> getOfferingsPage(Pageable pageable);

    OfferingDTO createOffering(OfferingRequest offeringRequest);

    OfferingDTO getOffering(Long offeringId);

    List<OfferingDTO> getOfferingsByUserId(Long userId);

    OfferingDTO updateOffering(Long offeringId, OfferingRequest offeringRequest);
}