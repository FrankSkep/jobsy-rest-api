package com.fran.jobsy.app.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MessageRequest(
        @NotNull(message = "El ID del remitente es obligatorio.")
        Long senderId,

        @NotBlank(message = "El contenido del mensaje no puede estar vacío.")
        String content
) {
}
