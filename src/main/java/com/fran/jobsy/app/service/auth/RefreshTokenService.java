package com.fran.jobsy.app.service.auth;

import com.fran.jobsy.app.entity.RefreshToken;
import com.fran.jobsy.app.entity.User;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);

    RefreshToken verifyExpiration(RefreshToken token);

    RefreshToken findByToken(String token);

    void revokeToken(String token);

    void revokeUserTokens(User user);
}
