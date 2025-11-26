package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.booking.AvailabilityCheckResponse;
import com.fran.jobsy.app.dto.booking.DailyAvailabilityResponse;
import com.fran.jobsy.app.service.provideravailability.ProviderAvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "ProviderAvailability", description = "Operaciones relacionadas con la disponibilidad de los proveedores")
public class ProviderAvailabilityController {

    private final ProviderAvailabilityService providerAvailabilityService;

    @GetMapping("/{providerId}/availability/check-day")
    @Operation(
            summary = "Verificar si un proveedor tiene disponibilidad en un día específico",
            description = "Endpoint público que verifica si un proveedor tiene al menos un horario libre en la fecha especificada. " +
                    "Retorna true si hay espacios disponibles, false en caso contrario."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Verificación completada exitosamente",
                    content = @Content(schema = @Schema(implementation = AvailabilityCheckResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Proveedor no encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fecha inválida (fecha pasada o muy adelantada)",
                    content = @Content
            )
    })
    public ResponseEntity<AvailabilityCheckResponse> checkDayAvailability(
            @Parameter(description = "ID del proveedor", required = true, example = "1")
            @PathVariable Long providerId,
            @Parameter(description = "Fecha a consultar (formato ISO: YYYY-MM-DD)", required = true, example = "2025-12-25")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        AvailabilityCheckResponse response = providerAvailabilityService.checkDayAvailability(providerId, date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{providerId}/availability/day")
    @Operation(
            summary = "Obtener la disponibilidad detallada de un proveedor para un día específico",
            description = "Endpoint público que devuelve todos los slots horarios del día solicitado, " +
                    "indicando cuáles están disponibles y cuáles están ocupados. " +
                    "Los slots se generan en intervalos de 1 hora basándose en los horarios de trabajo del proveedor."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Disponibilidad obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = DailyAvailabilityResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Proveedor no encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fecha inválida (fecha pasada o muy adelantada)",
                    content = @Content
            )
    })
    public ResponseEntity<DailyAvailabilityResponse> getAvailabilityForDay(
            @Parameter(description = "ID del proveedor", required = true, example = "1")
            @PathVariable Long providerId,
            @Parameter(description = "Fecha a consultar (formato ISO: YYYY-MM-DD)", required = true, example = "2025-12-25")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        DailyAvailabilityResponse response = providerAvailabilityService.getAvailabilityForDay(providerId, date);
        return ResponseEntity.ok(response);
    }
}
