package com.se2035.jrw.controller;

import com.se2035.jrw.entity.Candidate;
import com.se2035.jrw.entity.Company;
import com.se2035.jrw.entity.Recruiter;
import com.se2035.jrw.entity.User;
import com.se2035.jrw.enums.UserRole;
import com.se2035.jrw.enums.UserStatus;
import com.se2035.jrw.repository.CandidateRepository;
import com.se2035.jrw.repository.CompanyRepo;
import com.se2035.jrw.repository.RecruiterRepo;
import com.se2035.jrw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final RecruiterRepo recruiterRepo;
    private final CompanyRepo companyRepo;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showLoginPage(Principal principal) {
        if (principal != null) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Principal principal) {
        if (principal != null) {
            return "redirect:/";
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam String role,
            @RequestParam String phone,
            Model model) {

        // Validate phone: must be exactly 10 digits
        if (phone == null || !phone.matches("\\d{10}")) {
            model.addAttribute("error", "Số điện thoại phải có đúng 10 chữ số!");
            return "register";
        }

        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Email này đã được sử dụng!");
            return "register";
        }

        UserRole userRole;
        try {
            // Map seeker to CANDIDATE
            if ("JOBSEEKER".equalsIgnoreCase(role)) {
                userRole = UserRole.CANDIDATE;
            } else {
                userRole = UserRole.valueOf(role.toUpperCase());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Vai trò không hợp lệ!");
            return "register";
        }

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .role(userRole)
                .status(UserStatus.ACTIVE)
                .build();

        user = userRepository.save(user);

        if (userRole == UserRole.CANDIDATE) {
            Candidate candidate = Candidate.builder()
                    .user(user)
                    .fullName(fullName)
                    .phone(phone)
                    .build();
            candidateRepository.save(candidate);
        } else if (userRole == UserRole.RECRUITER) {
            // Associate with a default company seeded in the DB
            Company defaultCompany = companyRepo.findAll().stream().findFirst().orElse(null);
            if (defaultCompany == null) {
                // Create a temporary one if none exists
                defaultCompany = Company.builder()
                        .companyName("Chưa cập nhật")
                        .status("ACTIVE")
                        .build();
                defaultCompany = companyRepo.save(defaultCompany);
            }

            Recruiter recruiter = Recruiter.builder()
                    .user(user)
                    .company(defaultCompany)
                    .fullName(fullName)
                    .phone(phone)
                    .build();
            recruiterRepo.save(recruiter);
        }

        return "redirect:/login?success=true";
    }
}