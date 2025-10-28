package com.fran.jobsy.app.service.availabilityslot;

import com.fran.jobsy.app.dto.availabilityslot.AvailabilitySlotRequest;
import com.fran.jobsy.app.dto.availabilityslot.AvailabilitySlotResponse;

import java.util.List;

public interface AvailabilitySlotService {
    List<AvailabilitySlotResponse> getAllByUserId(Long providerId);

    AvailabilitySlotResponse create(AvailabilitySlotRequest availabilitySlotReq);

    AvailabilitySlotResponse update(Long slotId, AvailabilitySlotRequest availabilitySlotReq);

    void delete(Long slotId);
}
