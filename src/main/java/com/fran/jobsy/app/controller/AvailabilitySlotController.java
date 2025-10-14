package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotDTO;
import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotRequest;
import com.fran.jobsy.app.service.AvailabilitySlotService;
import com.fran.jobsy.app.util.RestUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class AvailabilitySlotController {

    private final AvailabilitySlotService availabilitySlotService;

    @GetMapping("/{id}/availability")
    public List<AvailabilitySlotDTO> getAvailabilitySlots(@PathVariable Long id) {
        return availabilitySlotService.getAllByUserId(id);
    }

    @PostMapping("/me/availability")
    public ResponseEntity<AvailabilitySlotDTO> createAvailabilitySlot(@RequestBody @Valid AvailabilitySlotRequest availabilitySlotReq) {
        AvailabilitySlotDTO slot = availabilitySlotService.create(availabilitySlotReq);
        URI location = RestUtils.buildCreatedLocation(slot.id());
        return ResponseEntity.created(location).body(slot);
    }

    @PutMapping("/me/availability/{slotId}")
    public ResponseEntity<AvailabilitySlotDTO> updateAvailabilitySlot(@PathVariable Long slotId, @RequestBody @Valid AvailabilitySlotRequest availabilitySlotReq) {
        AvailabilitySlotDTO slot = availabilitySlotService.update(slotId, availabilitySlotReq);
        return ResponseEntity.ok(slot);
    }

    @DeleteMapping("/me/availability/{slotId}")
    public ResponseEntity<Void> deleteAvailabilitySlot(@PathVariable Long slotId) {
        availabilitySlotService.delete(slotId);
        return ResponseEntity.noContent().build();
    }
}
