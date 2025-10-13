package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotDTO;
import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotRequest;
import com.fran.jobsy.app.service.AvailabilitySlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
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
    public ResponseEntity<AvailabilitySlotDTO> createAvailabilitySlot(@RequestBody AvailabilitySlotRequest availabilitySlotReq) throws URISyntaxException {
        AvailabilitySlotDTO slot = availabilitySlotService.create(availabilitySlotReq);
        return ResponseEntity.created(new URI("/api/v1/users/me/availability/" + slot.id())).body(slot);
    }

    @PutMapping("/me/availability/{slotId}")
    public ResponseEntity<AvailabilitySlotDTO> updateAvailabilitySlot(@PathVariable Long slotId, @RequestBody AvailabilitySlotRequest availabilitySlotReq) {
        AvailabilitySlotDTO slot = availabilitySlotService.update(slotId, availabilitySlotReq);
        return ResponseEntity.ok(slot);
    }

    @DeleteMapping("/me/availability/{slotId}")
    public ResponseEntity<Void> deleteAvailabilitySlot(@PathVariable Long slotId) {
        availabilitySlotService.delete(slotId);
        return ResponseEntity.noContent().build();
    }
}
