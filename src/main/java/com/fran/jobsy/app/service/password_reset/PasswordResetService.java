package com.fran.jobsy.app.service.password_reset;

public interface PasswordResetService {
    void sendResetLink(String email);

    void resetPassword(String token, String newPassword);
}