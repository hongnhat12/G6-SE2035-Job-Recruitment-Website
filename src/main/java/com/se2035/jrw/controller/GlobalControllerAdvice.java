package com.se2035.jrw.controller;

import com.se2035.jrw.entity.User;
import com.se2035.jrw.enums.UserRole;
import com.se2035.jrw.repository.UserRepository;
import com.se2035.jrw.repository.CandidateRepository;
import com.se2035.jrw.repository.RecruiterRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final RecruiterRepo recruiterRepo;

    @ModelAttribute
    public void addAttributes(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            userRepository.findByEmail(email).ifPresent(user -> {
                model.addAttribute("currentUser", user);
                if (user.getRole() == UserRole.CANDIDATE) {
                    candidateRepository.findByUser_UserId(user.getUserId()).ifPresent(c -> {
                        model.addAttribute("candidate", c);
                        model.addAttribute("fullName", c.getFullName());
                        model.addAttribute("profileImage", c.getProfileImage());
                        model.addAttribute("userRole", "CANDIDATE");
                    });
                } else if (user.getRole() == UserRole.RECRUITER) {
                    recruiterRepo.findByUser_UserId(user.getUserId()).ifPresent(r -> {
                        model.addAttribute("recruiter", r);
                        model.addAttribute("fullName", r.getFullName());
                        // Recruiter's logo can act as their avatar if company has logo
                        if (r.getCompany() != null && r.getCompany().getLogo() != null) {
                            model.addAttribute("profileImage", r.getCompany().getLogo());
                        }
                        model.addAttribute("userRole", "RECRUITER");
                    });
                } else if (user.getRole() == UserRole.ADMIN) {
                    model.addAttribute("fullName", "System Admin");
                    model.addAttribute("userRole", "ADMIN");
                }
            });
        }
    }
}
