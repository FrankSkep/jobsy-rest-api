package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.entity.PasswordResetToken;
import com.fran.jobsy.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    Iterable<PasswordResetToken> findByUserAndUsedFalse(User user);
}
