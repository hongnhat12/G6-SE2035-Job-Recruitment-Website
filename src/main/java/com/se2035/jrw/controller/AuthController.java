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

    private final UserRepo userRepo;
    private final CandidateRepo candidateRepo;
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

        if (phone == null || !phone.matches("\\d{10}")) {
            model.addAttribute("error", "Phone number must be exactly 10 digits!");
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

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .role(userRole)
                .status(UserStatus.ACTIVE)
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
            Company defaultCompany = companyRepo.findAll().stream().findFirst().orElse(null);
            if (defaultCompany == null) {
                defaultCompany = Company.builder()
                        .companyName("Not updated")
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