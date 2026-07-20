package com.se2035.jrw.controller;

import com.se2035.jrw.entity.CV;
import com.se2035.jrw.entity.Candidate;
import com.se2035.jrw.entity.Company;
import com.se2035.jrw.entity.Recruiter;
import com.se2035.jrw.entity.User;
import com.se2035.jrw.enums.UserRole;
import com.se2035.jrw.repository.CompanyRepo;
import com.se2035.jrw.repository.RecruiterRepo;
import com.se2035.jrw.repository.UserRepo;
import com.se2035.jrw.service.CVService;
import com.se2035.jrw.service.CandidateProfileService;
import com.se2035.jrw.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepo userRepo;
    private final RecruiterRepo recruiterRepo;
    private final CompanyRepo companyRepo;
    private final CandidateProfileService candidateProfileService;
    private final CVService cvService;
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
                Candidate candidate = candidateProfileService.getByUserId(user.getUserId());
                model.addAttribute("profile", candidate);
                model.addAttribute("accountEmail", user.getEmail());
                model.addAttribute("currentCv", cvService.getCurrentCv(candidate.getCandidateId()).orElse(null));
                return "profile/seeker";
            }

            if (user.getRole() == UserRole.RECRUITER) {
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


    @PostMapping("/seeker/avatar")
    public String uploadOrReplaceAvatar(
            @RequestParam("avatarFile") MultipartFile avatarFile,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        try {
            User user = getCurrentUser(auth);
            if (user.getRole() != UserRole.CANDIDATE) {
                throw new IllegalStateException("Only candidates can update a candidate avatar");
            }

            candidateProfileService.updateAvatar(user.getUserId(), avatarFile);
            redirectAttributes.addFlashAttribute("success", "Avatar updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Could not update avatar: " + e.getMessage());
        }
        return "redirect:/profile#profile-header";
    }

    @PostMapping("/seeker/update")
    public String updateSeekerProfile(
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthday,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String headline,
            @RequestParam(required = false) String summary,
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) String education,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        try {
            User user = getCurrentUser(auth);
            if (user.getRole() != UserRole.CANDIDATE) {
                throw new IllegalStateException("Only candidates can update a candidate profile");
            }

            candidateProfileService.updateProfile(
                    user.getUserId(),
                    fullName,
                    phone,
                    birthday,
                    gender,
                    address,
                    headline,
                    summary,
                    skills,
                    experience,
                    education
            );

            redirectAttributes.addFlashAttribute("success", "Candidate profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Could not update profile: " + e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/seeker/cv")
    public String uploadOrReplaceCv(
            @RequestParam("cvFile") MultipartFile cvFile,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        try {
            User user = getCurrentUser(auth);
            if (user.getRole() != UserRole.CANDIDATE) {
                throw new IllegalStateException("Only candidates can manage a CV");
            }

            Candidate candidate = candidateProfileService.getByUserId(user.getUserId());
            boolean replacing = cvService.getCurrentCv(candidate.getCandidateId()).isPresent();
            cvService.uploadOrReplaceCurrentCv(candidate.getCandidateId(), cvFile);

            redirectAttributes.addFlashAttribute(
                    "success",
                    replacing ? "CV replaced successfully!" : "CV uploaded successfully!"
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Could not upload CV: " + e.getMessage());
        }
        return "redirect:/profile#cv-section";
    }

    @GetMapping("/seeker/cv/view")
    public ResponseEntity<Resource> viewCurrentCv(Authentication auth) throws IOException {
        return buildCurrentCvResponse(auth, false);
    }

    @GetMapping("/seeker/cv/download")
    public ResponseEntity<Resource> downloadCurrentCv(Authentication auth) throws IOException {
        return buildCurrentCvResponse(auth, true);
    }

    private ResponseEntity<Resource> buildCurrentCvResponse(Authentication auth, boolean download) throws IOException {
        User user = getCurrentUser(auth);
        if (user.getRole() != UserRole.CANDIDATE) {
            return ResponseEntity.status(403).build();
        }

        Candidate candidate = candidateProfileService.getByUserId(user.getUserId());
        CV cv = cvService.getCurrentCv(candidate.getCandidateId()).orElse(null);
        if (cv == null) {
            return ResponseEntity.notFound().build();
        }

        Resource resource;
        try {
            resource = cvService.loadCvResource(cv);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }

        ContentDisposition.Builder dispositionBuilder = download
                ? ContentDisposition.attachment()
                : ContentDisposition.inline();
        ContentDisposition disposition = dispositionBuilder
                .filename(cv.getCvName(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(resource.contentLength())
                .cacheControl(CacheControl.noCache())
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(resource);
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
