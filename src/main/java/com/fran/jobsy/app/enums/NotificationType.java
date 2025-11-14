package com.fran.jobsy.app.enums;

import lombok.Getter;

@Getter
public enum NotificationType {

    // --- Reservas ---
    BOOKING_CREATED(true),
    BOOKING_CONFIRMED(true),
    BOOKING_CANCELLED(true),
    BOOKING_COMPLETED(false),

    // --- Reseñas ---
    REVIEW_RECEIVED(false),

    // --- Proveedor ---
    PROVIDER_REQUEST_SUBMITTED(false),
    PROVIDER_APPROVED(true),
    PROVIDER_REJECTED(true),

    // --- Comunicación ---
    MESSAGE_RECEIVED(false),

    // --- Sistema / Admin ---
    ADMIN_WARNING(true),
    ACCOUNT_DEACTIVATION(true),
    SYSTEM(false);

    private final boolean sendEmail;

    NotificationType(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }
}