package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotDTO;
import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotRequest;
import com.fran.jobsy.app.repository.AvailabilitySlotRepository;
import com.fran.jobsy.app.service.AvailabilitySlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilitySlotServiceImpl implements AvailabilitySlotService {

    private final AvailabilitySlotRepository availabilitySlotRepository;

    @Override
    public List<AvailabilitySlotDTO> getProviderAvailabilitySlots(Long userId) {
        return availabilitySlotRepository.findAllByUserId(userId);
    }

    @Override
    public AvailabilitySlotDTO createAvailabilitySlot(AvailabilitySlotRequest availabilitySlotReq) {
        return null;
    }

    @Override
    public AvailabilitySlotDTO updateAvailabilitySlot(Long slotId, AvailabilitySlotRequest availabilitySlotReq) {
        return null;
    }

    @Override
    public void deleteAvailabilitySlot(Long slotId) {

    }
}
