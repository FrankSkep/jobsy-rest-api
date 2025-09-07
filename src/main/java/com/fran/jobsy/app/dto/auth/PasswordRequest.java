package com.fran.jobsy.app.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PasswordRequest {
    private String oldPassword;
    private String newPassword;
}
