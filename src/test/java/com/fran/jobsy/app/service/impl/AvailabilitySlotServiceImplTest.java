package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotDTO;
import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotRequest;
import com.fran.jobsy.app.entity.AvailabilitySlot;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.mapper.AvailabilitySlotMapper;
import com.fran.jobsy.app.repository.AvailabilitySlotRepository;
import com.fran.jobsy.app.util.AuthenticatedUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AvailabilitySlotServiceImplTest {
    @Mock
    private AvailabilitySlotRepository availabilitySlotRepository;
    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;
    @Mock
    private AvailabilitySlotMapper availabilitySlotMapper;
    @InjectMocks
    private AvailabilitySlotServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AvailabilitySlotServiceImpl(availabilitySlotRepository, authenticatedUserProvider, availabilitySlotMapper);
    }

    @Test
    void testCreate() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        AvailabilitySlotRequest req = new AvailabilitySlotRequest(1, "09:00", "17:00");
        AvailabilitySlot slot = AvailabilitySlot.builder()
                .user(user)
                .weekday(1)
                .startTime("09:00")
                .endTime("17:00")
                .build();
        slot.setId(10L);

        when(authenticatedUserProvider.getAuthenticatedUserId()).thenReturn(userId);
        when(authenticatedUserProvider.getUserReference(userId)).thenReturn(user);
        when(availabilitySlotRepository.save(any(AvailabilitySlot.class))).thenAnswer(inv -> {
            AvailabilitySlot s = inv.getArgument(0);
            s.setId(10L);
            return s;
        });
        when(availabilitySlotMapper.toDTO(any(AvailabilitySlot.class))).thenAnswer(inv -> {
            AvailabilitySlot s = inv.getArgument(0);
            return new AvailabilitySlotDTO(s.getId(), s.getUser().getId(), s.getWeekday(), s.getStartTime(), s.getEndTime());
        });

        AvailabilitySlotDTO dto = service.create(req);
        assertEquals(10L, dto.id());
        assertEquals(userId, dto.providerId());
        assertEquals(1, dto.weekday());
        assertEquals("09:00", dto.startTime());
        assertEquals("17:00", dto.endTime());
    }

    @Test
    void testUpdate() {
        Long slotId = 2L;
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        AvailabilitySlot slot = AvailabilitySlot.builder()
                .user(user)
                .weekday(2)
                .startTime("10:00")
                .endTime("18:00")
                .build();
        slot.setId(slotId);
        AvailabilitySlotRequest req = new AvailabilitySlotRequest(3, "11:00", "19:00");

        when(availabilitySlotRepository.findById(slotId)).thenReturn(Optional.of(slot));
        when(availabilitySlotRepository.save(any(AvailabilitySlot.class))).thenAnswer(inv -> inv.getArgument(0));
        when(availabilitySlotMapper.toDTO(any(AvailabilitySlot.class))).thenAnswer(inv -> {
            AvailabilitySlot s = inv.getArgument(0);
            return new AvailabilitySlotDTO(s.getId(), s.getUser().getId(), s.getWeekday(), s.getStartTime(), s.getEndTime());
        });

        AvailabilitySlotDTO dto = service.update(slotId, req);
        assertEquals(slotId, dto.id());
        assertEquals(userId, dto.providerId());
        assertEquals(3, dto.weekday());
        assertEquals("11:00", dto.startTime());
        assertEquals("19:00", dto.endTime());
    }

    @Test
    void testDelete() {
        Long slotId = 3L;
        AvailabilitySlot slot = AvailabilitySlot.builder().build();
        slot.setId(slotId);
        when(availabilitySlotRepository.findById(slotId)).thenReturn(Optional.of(slot));
        doNothing().when(availabilitySlotRepository).deleteById(slotId);

        assertDoesNotThrow(() -> service.delete(slotId));
        verify(availabilitySlotRepository, times(1)).deleteById(slotId);
    }

    @Test
    void testGetAllByUserId() {
        Long userId = 1L;
        List<AvailabilitySlotDTO> dtos = Arrays.asList(
                new AvailabilitySlotDTO(1L, userId, 1, "09:00", "12:00"),
                new AvailabilitySlotDTO(2L, userId, 2, "13:00", "15:00")
        );
        when(availabilitySlotRepository.findAllByUserId(userId)).thenReturn(dtos);
        List<AvailabilitySlotDTO> result = service.getAllByUserId(userId);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(2L, result.get(1).id());
    }

    @Test
    void testUpdateNotFound() {
        when(availabilitySlotRepository.findById(99L)).thenReturn(Optional.empty());
        AvailabilitySlotRequest req = new AvailabilitySlotRequest(1, "09:00", "17:00");
        assertThrows(ResourceNotFoundException.class, () -> service.update(99L, req));
    }

    @Test
    void testDeleteNotFound() {
        when(availabilitySlotRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
    }
}
