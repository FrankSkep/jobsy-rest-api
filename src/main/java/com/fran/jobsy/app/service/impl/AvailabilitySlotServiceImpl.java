package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotDTO;
import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotRequest;
import com.fran.jobsy.app.entity.AvailabilitySlot;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.repository.AvailabilitySlotRepository;
import com.fran.jobsy.app.service.AvailabilitySlotService;
import com.fran.jobsy.app.utils.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilitySlotServiceImpl implements AvailabilitySlotService {

    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    public List<AvailabilitySlotDTO> getAllByUserId(Long userId) {
        return availabilitySlotRepository.findAllByUserId(userId);
    }

    @Override
    public AvailabilitySlotDTO create(AvailabilitySlotRequest availabilitySlotReq) {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();

        AvailabilitySlot newSlot = AvailabilitySlot.builder()
                .user(authenticatedUserProvider.getUserReference(userId))
                .weekday(availabilitySlotReq.weekday())
                .startTime(availabilitySlotReq.startTime())
                .endTime(availabilitySlotReq.endTime())
                .build();

        availabilitySlotRepository.save(newSlot);

        return toDto(newSlot);
    }

    @Override
    public AvailabilitySlotDTO update(Long slotId, AvailabilitySlotRequest availabilitySlotReq) {
        AvailabilitySlot slot = getAvailabilitySlotIfExists(slotId);

        slot.setWeekday(availabilitySlotReq.weekday());
        slot.setStartTime(availabilitySlotReq.startTime());
        slot.setEndTime(availabilitySlotReq.endTime());

        return toDto(availabilitySlotRepository.save(slot));
    }

    @Override
    public void delete(Long slotId) {
        getAvailabilitySlotIfExists(slotId);
        availabilitySlotRepository.deleteById(slotId);
    }

    private AvailabilitySlot getAvailabilitySlotIfExists(Long slotId) {
        return availabilitySlotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability slot not found with id: " + slotId));
    }

    private AvailabilitySlotDTO toDto(AvailabilitySlot slot) {
        return new AvailabilitySlotDTO(slot.getId(), slot.getUser().getId(),
                slot.getWeekday(), slot.getStartTime(), slot.getEndTime());
    }
}
