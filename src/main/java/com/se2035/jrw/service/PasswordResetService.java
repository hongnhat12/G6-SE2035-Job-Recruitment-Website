package com.se2035.jrw.service;

import com.se2035.jrw.entity.PasswordResetToken;
import com.se2035.jrw.entity.User;
import com.se2035.jrw.repository.PasswordResetTokenRepo;
import com.se2035.jrw.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final int TOKEN_VALID_MINUTES = 30;

    private final UserRepo userRepo;
    private final PasswordResetTokenRepo passwordResetTokenRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Optional<String> createResetLink(String email, String baseUrl) {
        return userRepo.findByEmail(email.trim().toLowerCase())
                .map(user -> {
                    passwordResetTokenRepo.deleteByUser(user);

                    String token = UUID.randomUUID().toString();
                    PasswordResetToken resetToken = PasswordResetToken.builder()
                            .token(token)
                            .user(user)
                            .expiryDate(LocalDateTime.now().plusMinutes(TOKEN_VALID_MINUTES))
                            .used(false)
                            .build();

                    passwordResetTokenRepo.save(resetToken);
                    return baseUrl + "/reset-password?token=" + token;
                });
    }

    @Transactional(readOnly = true)
    public boolean isValidToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        return passwordResetTokenRepo.findByTokenAndUsedFalse(token)
                .filter(resetToken -> !resetToken.isExpired())
                .isPresent();
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        if (token == null || token.isBlank()) {
            return false;
        }

        Optional<PasswordResetToken> resetTokenOptional = passwordResetTokenRepo.findByTokenAndUsedFalse(token)
                .filter(resetToken -> !resetToken.isExpired());

        if (resetTokenOptional.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = resetTokenOptional.get();
        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepo.save(resetToken);

        return true;
    }
}
