package com.se2035.jrw.repository;

import com.se2035.jrw.entity.PasswordResetToken;
import com.se2035.jrw.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepo extends JpaRepository<PasswordResetToken, Integer> {
    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);

    void deleteByUser(User user);

    void deleteByExpiryDateBefore(LocalDateTime expiryDate);
}
