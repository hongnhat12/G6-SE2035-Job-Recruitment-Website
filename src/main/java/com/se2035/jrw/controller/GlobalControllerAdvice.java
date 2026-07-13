package com.se2035.jrw.controller;

import com.se2035.jrw.entity.User;
import com.se2035.jrw.enums.UserRole;
import com.se2035.jrw.repository.UserRepo;
import com.se2035.jrw.repository.CandidateRepo;
import com.se2035.jrw.repository.RecruiterRepo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UserRepo userRepo;
    private final CandidateRepo candidateRepo;
    private final RecruiterRepo recruiterRepo;
    private final HttpServletRequest request;

    @ModelAttribute
    public void addAttributes(Model model, Principal principal) {
        if (request != null) {
            String uri = request.getRequestURI();
            model.addAttribute("isAdminPage", uri != null && uri.startsWith("/admin"));
        }
        if (principal != null) {
            String email = principal.getName();
            userRepo.findByEmail(email).ifPresent(user -> {
                model.addAttribute("currentUser", user);
                if (user.getRole() == UserRole.CANDIDATE) {
                    candidateRepo.findByUser_UserId(user.getUserId()).ifPresent(c -> {
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
