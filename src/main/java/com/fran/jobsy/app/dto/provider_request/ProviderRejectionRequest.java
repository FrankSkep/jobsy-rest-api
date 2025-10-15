package com.fran.jobsy.app.dto.provider_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProviderRejectionRequest(
        @NotBlank(message = "Debe indicar la razón del rechazo")
        @Size(max = 300, message = "La razón no debe superar los 300 caracteres")
        String reason
) {
}

