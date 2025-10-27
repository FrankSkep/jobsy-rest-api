package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.auth.TokenResponse;
import com.fran.jobsy.app.dto.auth.LoginRequest;
import com.fran.jobsy.app.dto.auth.RegisterRequest;

public interface AuthService {

    TokenResponse login(LoginRequest loginRequest);

    TokenResponse register(RegisterRequest registerRequest);
}
