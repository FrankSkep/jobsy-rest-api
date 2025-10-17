package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.offering.OfferingDTO;
import com.fran.jobsy.app.dto.offering.OfferingFilterDTO;
import com.fran.jobsy.app.dto.offering.OfferingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OfferingService {

    List<OfferingDTO> getServices();

    Page<OfferingDTO> getServicesPaged(Pageable pageable);

    List<OfferingDTO> getServicesWithFilters(OfferingFilterDTO filters);

    Page<OfferingDTO> getServicesWithFiltersPaged(OfferingFilterDTO filters, Pageable pageable);

    List<OfferingDTO> getServicesByUserId(Long userId);

    OfferingDTO createOffering(OfferingRequest offeringRequest);

}