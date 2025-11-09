package com.fran.jobsy.app.service.passwordreset;

public interface PasswordResetService {
    void sendResetLink(String email);

    void resetPassword(String token, String newPassword);

    void validateResetToken(String token);
}