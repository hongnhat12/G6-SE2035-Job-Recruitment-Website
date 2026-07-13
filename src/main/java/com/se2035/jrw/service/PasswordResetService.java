package com.se2035.jrw.service;

import java.util.Optional;

public interface PasswordResetService {

    Optional<String> createResetLink(String email, String baseUrl);

    boolean isValidToken(String token);

    boolean resetPassword(String token, String newPassword);
}
