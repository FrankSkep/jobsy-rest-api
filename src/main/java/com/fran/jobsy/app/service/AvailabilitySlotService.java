package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotDTO;
import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotRequest;

import java.util.List;

public interface AvailabilitySlotService {
    List<AvailabilitySlotDTO> getProviderAvailabilitySlots(Long providerId);

    AvailabilitySlotDTO createAvailabilitySlot(AvailabilitySlotRequest availabilitySlotReq);

    AvailabilitySlotDTO updateAvailabilitySlot(Long slotId, AvailabilitySlotRequest availabilitySlotReq);

    void deleteAvailabilitySlot(Long slotId);
}
