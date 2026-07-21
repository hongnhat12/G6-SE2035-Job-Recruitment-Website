package com.se2035.jrw.service;

import com.se2035.jrw.entity.User;

import java.util.Optional;

public interface EmailVerificationService {

    String createVerificationLink(User user, String baseUrl);

    Optional<String> resendVerificationLink(String email, String baseUrl);

    boolean verifyEmail(String token);
}
