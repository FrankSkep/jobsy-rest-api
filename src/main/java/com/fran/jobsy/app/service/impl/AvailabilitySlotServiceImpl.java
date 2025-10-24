package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotDTO;
import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotRequest;
import com.fran.jobsy.app.entity.AvailabilitySlot;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.Role;
import com.fran.jobsy.app.exception.custom.ResourceAlreadyExistsException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.exception.custom.UnauthorizedAccessException;
import com.fran.jobsy.app.mapper.AvailabilitySlotMapper;
import com.fran.jobsy.app.repository.AvailabilitySlotRepository;
import com.fran.jobsy.app.repository.UserRepository;
import com.fran.jobsy.app.service.AvailabilitySlotService;
import com.fran.jobsy.app.util.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilitySlotServiceImpl implements AvailabilitySlotService {

    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final AvailabilitySlotMapper availabilitySlotMapper;
    private final UserRepository userRepository;

    @Override
    public List<AvailabilitySlotDTO> getAllByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        if (user.getRole() != Role.PROVIDER) {
            throw new UnauthorizedAccessException("El usuario con ID: " + userId + " no es un proveedor");
        }
        return availabilitySlotRepository.findAllByUserId(userId);
    }

    @Override
    public AvailabilitySlotDTO create(AvailabilitySlotRequest availabilitySlotReq) {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();

        if (availabilitySlotRepository.findOverlappingSlot(
                userId,
                availabilitySlotReq.weekday(),
                availabilitySlotReq.startTime(),
                availabilitySlotReq.endTime()
        ).isPresent()) {
            throw new ResourceAlreadyExistsException("Ya existe una franja horaria que se solapa");
        }

        AvailabilitySlot newSlot = AvailabilitySlot.builder()
                .user(authenticatedUserProvider.getUserReference(userId))
                .weekday(availabilitySlotReq.weekday())
                .startTime(availabilitySlotReq.startTime())
                .endTime(availabilitySlotReq.endTime())
                .build();

        availabilitySlotRepository.save(newSlot);

        return availabilitySlotMapper.toDTO(newSlot);
    }

    @Override
    public AvailabilitySlotDTO update(Long slotId, AvailabilitySlotRequest availabilitySlotReq) {
        AvailabilitySlot slot = getAvailabilitySlotIfExists(slotId);

        slot.setWeekday(availabilitySlotReq.weekday());
        slot.setStartTime(availabilitySlotReq.startTime());
        slot.setEndTime(availabilitySlotReq.endTime());

        return availabilitySlotMapper.toDTO(availabilitySlotRepository.save(slot));
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
}
