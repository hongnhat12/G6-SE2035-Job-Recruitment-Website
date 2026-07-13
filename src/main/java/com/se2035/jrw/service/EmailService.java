package com.se2035.jrw.service;

public interface EmailService {

    boolean isMailEnabled();

    void sendVerificationEmail(String toEmail, String verificationLink);

    void sendPasswordResetEmail(String toEmail, String resetLink);
}
