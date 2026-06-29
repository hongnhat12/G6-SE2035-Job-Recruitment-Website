package com.se2035.jrw.controller;

import com.se2035.jrw.entity.*;
import com.se2035.jrw.enums.UserRole;
import com.se2035.jrw.repository.*;
import com.se2035.jrw.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepo userRepo;
    private final CandidateRepo candidateRepo;
    private final RecruiterRepo recruiterRepo;
    private final CompanyRepo companyRepo;
    private final CVRepo cvRepo;
    private final FileStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder;

    private User getCurrentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("You are not logged in");
        }
        return userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    @GetMapping
    public String viewProfile(Authentication auth, Model model) {
        try {
            User user = getCurrentUser(auth);
            if (user.getRole() == UserRole.CANDIDATE) {
                Candidate candidate = candidateRepo.findByUser_UserId(user.getUserId())
                        .orElseThrow(() -> new IllegalStateException("Candidate profile not found"));
                model.addAttribute("profile", candidate);
                model.addAttribute("cvList", candidate.getCvList());
                return "profile/seeker";
            } else if (user.getRole() == UserRole.RECRUITER) {
                Recruiter recruiter = recruiterRepo.findByUser_UserId(user.getUserId())
                        .orElseThrow(() -> new IllegalStateException("Recruiter profile not found"));
                model.addAttribute("profile", recruiter);
                model.addAttribute("companies", companyRepo.findAll());
                return "profile/recruiter";
            }
            return "redirect:/";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @PostMapping("/seeker/update")
    public String updateSeekerProfile(
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam(required = false) String headline,
            @RequestParam(required = false) String summary,
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) String address,
            @RequestParam("cvFile") MultipartFile cvFile,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        try {
            User user = getCurrentUser(auth);
            Candidate candidate = candidateRepo.findByUser_UserId(user.getUserId())
                    .orElseThrow(() -> new IllegalStateException("Profile not found"));

            candidate.setFullName(fullName);
            candidate.setPhone(phone);
            candidate.setHeadline(headline);
            candidate.setSummary(summary);
            candidate.setSkills(skills);
            candidate.setAddress(address);

            candidateRepo.save(candidate);

            if (cvFile != null && !cvFile.isEmpty()) {
                String uploadedPath = fileStorageService.uploadFile(cvFile, "cvs");
                candidate.getCvList().forEach(c -> {
                    c.setIsDefault(false);
                    cvRepo.save(c);
                });

                CV newCv = CV.builder()
                        .candidate(candidate)
                        .cvName(cvFile.getOriginalFilename())
                        .filePath(uploadedPath)
                        .isDefault(true)
                        .build();
                cvRepo.save(newCv);
            }

            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/recruiter/update")
    public String updateRecruiterProfile(
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam(required = false) String position,
            @RequestParam Integer companyId,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String website,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String companyDescription,
            @RequestParam("logoFile") MultipartFile logoFile,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        try {
            User user = getCurrentUser(auth);
            Recruiter recruiter = recruiterRepo.findByUser_UserId(user.getUserId())
                    .orElseThrow(() -> new IllegalStateException("Profile not found"));

            recruiter.setFullName(fullName);
            recruiter.setPhone(phone);
            recruiter.setPosition(position);

            Company company = companyRepo.findById(companyId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid company"));
            
            if (companyName != null && !companyName.trim().isEmpty()) {
                company.setCompanyName(companyName.trim());
            }
            if (website != null) {
                company.setWebsite(website.trim());
            }
            if (address != null) {
                company.setAddress(address.trim());
            }
            if (companyDescription != null) {
                company.setDescription(companyDescription.trim());
            }

            if (logoFile != null && !logoFile.isEmpty()) {
                String logoPath = fileStorageService.uploadFile(logoFile, "logos");
                company.setLogo(logoPath);
            }

            companyRepo.save(company);
            recruiter.setCompany(company);
            recruiterRepo.save(recruiter);

            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        try {
            User user = getCurrentUser(auth);

            if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
                redirectAttributes.addFlashAttribute("error", "Current password is incorrect!");
                return "redirect:/profile#change-password";
            }

            if (newPassword == null || newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "New password must be at least 6 characters!");
                return "redirect:/profile#change-password";
            }

            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Confirm password does not match!");
                return "redirect:/profile#change-password";
            }

            user.setPasswordHash(passwordEncoder.encode(newPassword));
            userRepo.save(user);

            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/profile";
    }
}
