package com.se2035.jrw.service;

import com.se2035.jrw.entity.EmailVerificationToken;
import com.se2035.jrw.entity.User;
import com.se2035.jrw.enums.UserStatus;
import com.se2035.jrw.repository.EmailVerificationTokenRepo;
import com.se2035.jrw.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private static final int TOKEN_VALID_HOURS = 24;

    private final EmailVerificationTokenRepo emailVerificationTokenRepo;
    private final UserRepo userRepo;

    @Override
    @Transactional
    public String createVerificationLink(User user, String baseUrl) {
        emailVerificationTokenRepo.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(TOKEN_VALID_HOURS))
                .used(false)
                .build();

        emailVerificationTokenRepo.save(verificationToken);
        return baseUrl + "/verify-email?token=" + token;
    }

    @Override
    @Transactional
    public Optional<String> resendVerificationLink(String email, String baseUrl) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        return userRepo.findByEmail(email.trim().toLowerCase())
                .filter(user -> user.getStatus() != UserStatus.ACTIVE)
                .map(user -> createVerificationLink(user, baseUrl));
    }

    @Override
    @Transactional
    public boolean verifyEmail(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        Optional<EmailVerificationToken> tokenOptional = emailVerificationTokenRepo.findByTokenAndUsedFalse(token)
                .filter(emailToken -> !emailToken.isExpired());

        if (tokenOptional.isEmpty()) {
            return false;
        }

        EmailVerificationToken emailToken = tokenOptional.get();
        User user = emailToken.getUser();
        user.setStatus(UserStatus.ACTIVE);
        userRepo.save(user);

        emailToken.setUsed(true);
        emailVerificationTokenRepo.save(emailToken);

        return true;
    }
}
