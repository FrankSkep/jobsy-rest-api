package com.fran.jobsy.app.dto.providerrequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProviderApplyRequest(
        @NotBlank(message = "Bio is mandatory")
        @Size(max = 500, message = "Bio must not exceed 500 characters")
        String bio,

        @NotBlank(message = "Address is mandatory")
        @Size(max = 255, message = "Address must not exceed 255 characters")
        String addressText,

        @NotBlank(message = "RFC homoclave es obligatorio")
        @Pattern(regexp = "^[A-ZÑ&]{3,4}[0-9]{6}[A-Z0-9]{3}$", message = "RFC homoclave inválido")
        String rfcHomoclave,

        @NotBlank(message = "CURP is mandatory")
        @Pattern(regexp = "^[A-Z]{4}[0-9]{6}[A-Z]{6}[0-9A-Z]{2}$", message = "CURP inválido")
        String curp
) {
}
