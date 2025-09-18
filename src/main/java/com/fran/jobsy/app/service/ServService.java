package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.service.ServiceDTO;
import com.fran.jobsy.app.dto.service.ServiceFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServService {

    List<ServiceDTO> getServices();

    Page<ServiceDTO> getServicesPaged(Pageable pageable);

    List<ServiceDTO> getServicesWithFilters(ServiceFilterDTO filters);

    Page<ServiceDTO> getServicesWithFiltersPaged(ServiceFilterDTO filters, Pageable pageable);

    List<ServiceDTO> getServicesByUserId(Long userId);

}