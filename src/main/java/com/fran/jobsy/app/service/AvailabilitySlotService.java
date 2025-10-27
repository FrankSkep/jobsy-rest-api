package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotResponse;
import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotRequest;

import java.util.List;

public interface AvailabilitySlotService {
    List<AvailabilitySlotResponse> getAllByUserId(Long providerId);

    AvailabilitySlotResponse create(AvailabilitySlotRequest availabilitySlotReq);

    AvailabilitySlotResponse update(Long slotId, AvailabilitySlotRequest availabilitySlotReq);

    void delete(Long slotId);
}
