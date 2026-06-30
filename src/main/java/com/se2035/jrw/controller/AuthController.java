package com.se2035.jrw.controller;

import com.se2035.jrw.entity.Candidate;
import com.se2035.jrw.entity.Company;
import com.se2035.jrw.entity.Recruiter;
import com.se2035.jrw.entity.User;
import com.se2035.jrw.enums.UserRole;
import com.se2035.jrw.enums.UserStatus;
import com.se2035.jrw.repository.CandidateRepo;
import com.se2035.jrw.repository.CompanyRepo;
import com.se2035.jrw.repository.RecruiterRepo;
import com.se2035.jrw.repository.UserRepo;
import com.se2035.jrw.service.PasswordResetService;
import com.se2035.jrw.service.EmailVerificationService;
import com.se2035.jrw.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepo userRepo;
    private final CandidateRepo candidateRepo;
    private final RecruiterRepo recruiterRepo;
    private final CompanyRepo companyRepo;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetService passwordResetService;
    private final EmailVerificationService emailVerificationService;
    private final EmailService emailService;

    @GetMapping("/login")
    public String showLoginPage(Principal principal) {
        if (principal != null) {
            return "redirect:/";
        }
        return "login";
    }


    @GetMapping("/forgot-password")
    public String showForgotPasswordPage(Principal principal) {
        if (principal != null) {
            return "redirect:/";
        }
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam String email,
            HttpServletRequest request,
            Model model) {

        String baseUrl = getBaseUrl(request);

        passwordResetService.createResetLink(email, baseUrl)
                .ifPresent(resetLink -> {
                    if (emailService.isMailEnabled()) {
                        try {
                            emailService.sendPasswordResetEmail(email.trim().toLowerCase(), resetLink);
                        } catch (MailException e) {
                            model.addAttribute("error", "Could not send reset email. Please check SMTP configuration.");
                            model.addAttribute("resetLink", resetLink);
                        }
                    } else {
                        model.addAttribute("resetLink", resetLink);
                    }
                });

        model.addAttribute("message", emailService.isMailEnabled()
                ? "If this email exists, a password reset email has been sent."
                : "If this email exists, a password reset link has been created. Demo mode is showing the link below.");
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam String token, Model model, Principal principal) {
        if (principal != null) {
            return "redirect:/";
        }

        if (!passwordResetService.isValidToken(token)) {
            model.addAttribute("error", "Password reset link is invalid or expired.");
            return "reset-password";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam String token,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        if (password == null || password.length() < 6) {
            model.addAttribute("token", token);
            model.addAttribute("error", "Password must be at least 6 characters.");
            return "reset-password";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("token", token);
            model.addAttribute("error", "Password confirmation does not match.");
            return "reset-password";
        }

        if (!passwordResetService.resetPassword(token, password)) {
            model.addAttribute("error", "Password reset link is invalid or expired.");
            return "reset-password";
        }

        return "redirect:/login?resetSuccess=true";
    }


    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token) {
        if (emailVerificationService.verifyEmail(token)) {
            return "redirect:/login?verified=true";
        }
        return "redirect:/login?verifyError=true";
    }

    @PostMapping("/resend-verification")
    public String resendVerification(
            @RequestParam String email,
            HttpServletRequest request,
            Model model) {

        String baseUrl = getBaseUrl(request);
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();

        emailVerificationService.resendVerificationLink(normalizedEmail, baseUrl)
                .ifPresent(verificationLink -> {
                    if (emailService.isMailEnabled()) {
                        try {
                            emailService.sendVerificationEmail(normalizedEmail, verificationLink);
                        } catch (MailException e) {
                            model.addAttribute("error", "Could not send verification email. Please check SMTP configuration.");
                            model.addAttribute("verificationLink", verificationLink);
                        }
                    } else {
                        model.addAttribute("verificationLink", verificationLink);
                    }
                });

        model.addAttribute("message", emailService.isMailEnabled()
                ? "If this email exists and is not verified yet, a new verification email has been sent."
                : "If this email exists and is not verified yet, a new verification link has been created. Demo mode is showing the link below.");
        model.addAttribute("email", normalizedEmail);
        return "verify-email-sent";
    }

    @GetMapping("/register")
    public String showRegisterPage(Principal principal, Model model) {
        if (principal != null) {
            return "redirect:/";
        }
        model.addAttribute("selectedRole", "CANDIDATE");
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam String fullName,
            @RequestParam String role,
            @RequestParam String phone,
            @RequestParam(required = false) String companyName,
            HttpServletRequest request,
            Model model) {

        email = email == null ? "" : email.trim().toLowerCase();
        fullName = fullName == null ? "" : fullName.trim();
        phone = phone == null ? "" : phone.trim();
        role = role == null ? "" : role.trim();
        companyName = companyName == null ? "" : companyName.trim();

        keepRegisterForm(model, email, fullName, phone, role, companyName);

        if (fullName.isBlank()) {
            model.addAttribute("error", "Full name is required!");
            return "register";
        }

        if (email.isBlank() || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            model.addAttribute("error", "Please enter a valid email address!");
            return "register";
        }

        if (phone.isBlank() || !phone.matches("\\d{10}")) {
            model.addAttribute("error", "Phone number must be exactly 10 digits!");
            return "register";
        }

        if (password == null || password.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters!");
            return "register";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Password confirmation does not match!");
            return "register";
        }

        if (userRepo.findByEmail(email).isPresent()) {
            model.addAttribute("error", "This email is already in use!");
            return "register";
        }

        UserRole userRole;
        try {
            if ("JOBSEEKER".equalsIgnoreCase(role)) {
                userRole = UserRole.CANDIDATE;
            } else {
                userRole = UserRole.valueOf(role.toUpperCase());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Invalid role!");
            return "register";
        }

        if (userRole == UserRole.ADMIN) {
            model.addAttribute("error", "Admin accounts cannot be created from this form!");
            return "register";
        }

        if (userRole == UserRole.RECRUITER && companyName.isBlank()) {
            model.addAttribute("error", "Company name is required for recruiter accounts!");
            return "register";
        }

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .role(userRole)
                .status(UserStatus.INACTIVE)
                .build();

        user = userRepo.save(user);

        if (userRole == UserRole.CANDIDATE) {
            Candidate candidate = Candidate.builder()
                    .user(user)
                    .fullName(fullName)
                    .phone(phone)
                    .build();
            candidateRepo.save(candidate);
        } else if (userRole == UserRole.RECRUITER) {
            Company company = getOrCreateCompany(companyName, email, phone);

            Recruiter recruiter = Recruiter.builder()
                    .user(user)
                    .company(company)
                    .fullName(fullName)
                    .phone(phone)
                    .build();
            recruiterRepo.save(recruiter);
        }

        String verificationLink = emailVerificationService.createVerificationLink(user, getBaseUrl(request));

        if (emailService.isMailEnabled()) {
            try {
                emailService.sendVerificationEmail(email, verificationLink);
            } catch (MailException e) {
                model.addAttribute("error", "Account created, but the verification email could not be sent. Please check SMTP configuration.");
                model.addAttribute("verificationLink", verificationLink);
            }
        } else {
            model.addAttribute("verificationLink", verificationLink);
        }

        model.addAttribute("email", email);
        model.addAttribute("message", emailService.isMailEnabled()
                ? "Account created successfully. Please check your email to verify your account before logging in."
                : "Account created successfully. Please verify your email before logging in. Demo mode is showing the link below.");
        return "verify-email-sent";
    }


    private String getBaseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() +
                (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort());
    }

    private void keepRegisterForm(Model model, String email, String fullName, String phone, String role, String companyName) {
        String selectedRole = "JOBSEEKER".equalsIgnoreCase(role) ? "CANDIDATE" : role.toUpperCase();
        model.addAttribute("email", email);
        model.addAttribute("fullName", fullName);
        model.addAttribute("phone", phone);
        model.addAttribute("selectedRole", selectedRole);
        model.addAttribute("companyName", companyName);
    }

    private Company getOrCreateCompany(String companyName, String recruiterEmail, String recruiterPhone) {
        final String normalizedCompanyName = (companyName == null || companyName.isBlank())
                ? "Not updated"
                : companyName.trim();

        return companyRepo.findByCompanyNameIgnoreCase(normalizedCompanyName)
                .orElseGet(() -> companyRepo.save(Company.builder()
                        .companyName(normalizedCompanyName)
                        .email(recruiterEmail)
                        .phone(recruiterPhone)
                        .status("ACTIVE")
                        .build()));
    }
}
