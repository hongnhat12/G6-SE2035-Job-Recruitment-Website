package com.se2035.jrw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    public boolean isMailEnabled() {
        return mailEnabled;
    }

    public void sendVerificationEmail(String toEmail, String verificationLink) {
        if (!mailEnabled) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Verify your Job Recruitment account");
        message.setText(
                "Hello,\n\n" +
                "Thank you for registering a Job Recruitment account.\n" +
                "Please click the link below to verify your email:\n\n" +
                verificationLink + "\n\n" +
                "This verification link will expire in 24 hours.\n\n" +
                "If you did not create this account, please ignore this email."
        );

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        if (!mailEnabled) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Reset your Job Recruitment password");
        message.setText(
                "Hello,\n\n" +
                "We received a request to reset your password.\n" +
                "Please click the link below to create a new password:\n\n" +
                resetLink + "\n\n" +
                "This reset link will expire in 30 minutes.\n\n" +
                "If you did not request this, please ignore this email."
        );

        mailSender.send(message);
    }
}
