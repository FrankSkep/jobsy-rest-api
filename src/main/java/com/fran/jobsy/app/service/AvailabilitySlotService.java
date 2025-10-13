package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotDTO;
import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotRequest;

import java.util.List;

public interface AvailabilitySlotService {
    List<AvailabilitySlotDTO> getAllByUserId(Long providerId);

    AvailabilitySlotDTO create(AvailabilitySlotRequest availabilitySlotReq);

    AvailabilitySlotDTO update(Long slotId, AvailabilitySlotRequest availabilitySlotReq);

    void delete(Long slotId);
}
