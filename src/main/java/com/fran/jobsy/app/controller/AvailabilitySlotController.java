package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotDTO;
import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotRequest;
import com.fran.jobsy.app.service.AvailabilitySlotService;
import com.fran.jobsy.app.util.RestUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Availability Slots", description = "Gestión de horarios de disponibilidad de los proveedores")
public class AvailabilitySlotController {

    private final AvailabilitySlotService availabilitySlotService;

    @GetMapping("/{id}/availability")
    @Operation(summary = "Obtener disponibilidad de usuario", description = "Devuelve la lista de horarios de disponibilidad de un usuario público.")
    public ResponseEntity<List<AvailabilitySlotDTO>> getAvailabilitySlots(@PathVariable Long id) {
        return ResponseEntity.ok(availabilitySlotService.getAllByUserId(id));
    }

    @PostMapping("/me/availability")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Crear horario de disponibilidad", description = "Permite a un usuario con rol PROVIDER crear un horario de disponibilidad. Requiere autenticación y rol PROVIDER.")
    public ResponseEntity<AvailabilitySlotDTO> createAvailabilitySlot(@RequestBody @Valid AvailabilitySlotRequest availabilitySlotReq) {
        AvailabilitySlotDTO slot = availabilitySlotService.create(availabilitySlotReq);
        URI location = RestUtils.buildCreatedLocation(slot.id());
        return ResponseEntity.created(location).body(slot);
    }

    @PutMapping("/me/availability/{slotId}")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Actualizar horario de disponibilidad", description = "Permite a un usuario con rol PROVIDER actualizar un horario de disponibilidad. Requiere autenticación y rol PROVIDER.")
    public ResponseEntity<AvailabilitySlotDTO> updateAvailabilitySlot(@PathVariable Long slotId, @RequestBody @Valid AvailabilitySlotRequest availabilitySlotReq) {
        AvailabilitySlotDTO slot = availabilitySlotService.update(slotId, availabilitySlotReq);
        return ResponseEntity.ok(slot);
    }

    @DeleteMapping("/me/availability/{slotId}")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Eliminar horario de disponibilidad", description = "Permite a un usuario con rol PROVIDER eliminar un horario de disponibilidad. Requiere autenticación y rol PROVIDER.")
    public ResponseEntity<Void> deleteAvailabilitySlot(@PathVariable Long slotId) {
        availabilitySlotService.delete(slotId);
        return ResponseEntity.noContent().build();
    }
}
