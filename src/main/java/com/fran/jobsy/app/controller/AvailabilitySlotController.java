package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.common.UriBuilder;
import com.fran.jobsy.app.dto.availabilityslot.AvailabilitySlotRequest;
import com.fran.jobsy.app.dto.availabilityslot.AvailabilitySlotResponse;
import com.fran.jobsy.app.service.availabilityslot.AvailabilitySlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @Operation(
            summary = "Obtener horarios de disponibilidad de un proveedor",
            description = "Endpoint público que devuelve todos los horarios de disponibilidad configurados por un proveedor. " +
                    "Los horarios se definen por día de la semana (1=Lunes, 7=Domingo) con hora de inicio y fin. " +
                    "Solo usuarios con rol PROVIDER pueden tener horarios de disponibilidad."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de horarios obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = AvailabilitySlotResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "El usuario no es un proveedor",
                    content = @Content
            )
    })
    public ResponseEntity<List<AvailabilitySlotResponse>> getAvailabilitySlots(
            @Parameter(description = "ID del usuario proveedor", required = true, example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(availabilitySlotService.getAllByUserId(id));
    }

    @PostMapping("/me/availability")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(
            summary = "Crear un nuevo horario de disponibilidad",
            description = "Permite a un proveedor autenticado crear un nuevo horario de disponibilidad. " +
                    "Se debe especificar el día de la semana (1=Lunes a 7=Domingo), hora de inicio y hora de fin. " +
                    "El sistema valida que no existan solapamientos con otros horarios del mismo día. " +
                    "**Requiere autenticación y rol PROVIDER.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Horario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = AvailabilitySlotResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o ya existe un horario que se solapa",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No tiene rol PROVIDER",
                    content = @Content
            )
    })
    public ResponseEntity<AvailabilitySlotResponse> createAvailabilitySlot(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del horario de disponibilidad a crear",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AvailabilitySlotRequest.class))
            )
            @RequestBody @Valid AvailabilitySlotRequest availabilitySlotReq
    ) {
        AvailabilitySlotResponse slot = availabilitySlotService.create(availabilitySlotReq);
        URI location = UriBuilder.buildCreatedLocation(slot.id());
        return ResponseEntity.created(location).body(slot);
    }

    @PutMapping("/me/availability/{slotId}")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(
            summary = "Actualizar un horario de disponibilidad existente",
            description = "Permite a un proveedor autenticado actualizar uno de sus horarios de disponibilidad. " +
                    "Se pueden modificar el día de la semana, hora de inicio y hora de fin. " +
                    "**Requiere autenticación y rol PROVIDER.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Horario actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = AvailabilitySlotResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No tiene rol PROVIDER",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Horario no encontrado",
                    content = @Content
            )
    })
    public ResponseEntity<AvailabilitySlotResponse> updateAvailabilitySlot(
            @Parameter(description = "ID del horario a actualizar", required = true, example = "1")
            @PathVariable Long slotId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos del horario de disponibilidad",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AvailabilitySlotRequest.class))
            )
            @RequestBody @Valid AvailabilitySlotRequest availabilitySlotReq
    ) {
        AvailabilitySlotResponse slot = availabilitySlotService.update(slotId, availabilitySlotReq);
        return ResponseEntity.ok(slot);
    }

    @DeleteMapping("/me/availability/{slotId}")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(
            summary = "Eliminar un horario de disponibilidad",
            description = "Permite a un proveedor autenticado eliminar uno de sus horarios de disponibilidad. " +
                    "Esta acción es permanente y no se puede deshacer. " +
                    "**Requiere autenticación y rol PROVIDER.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Horario eliminado exitosamente",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No tiene rol PROVIDER",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Horario no encontrado",
                    content = @Content
            )
    })
    public ResponseEntity<Void> deleteAvailabilitySlot(
            @Parameter(description = "ID del horario a eliminar", required = true, example = "1")
            @PathVariable Long slotId
    ) {
        availabilitySlotService.delete(slotId);
        return ResponseEntity.noContent().build();
    }
}
