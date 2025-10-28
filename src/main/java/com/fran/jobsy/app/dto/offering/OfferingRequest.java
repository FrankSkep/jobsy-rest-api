package com.fran.jobsy.app.dto.offering;

import com.fran.jobsy.app.dto.category.CategoryDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record OfferingRequest(
        @NotNull(message = "Category is mandatory")
        @Valid
        CategoryDTO category,

        @NotBlank(message = "Title is mandatory")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,

        @NotBlank(message = "Description is mandatory")
        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        @NotNull(message = "Base price is mandatory")
        @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than 0")
        Double basePrice,

        @NotNull(message = "Years of experience is mandatory")
        @Min(value = 0, message = "Years of experience cannot be negative")
        @Max(value = 50, message = "Years of experience must not exceed 50")
        Integer yearsOfExperience
) {
}
