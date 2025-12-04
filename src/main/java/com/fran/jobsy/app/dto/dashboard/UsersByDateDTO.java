package com.fran.jobsy.app.dto.dashboard;

import java.time.LocalDate;

public record UsersByDateDTO(
        LocalDate date,
        long count,
        long users,
        long providers,
        long admins
) {
}

