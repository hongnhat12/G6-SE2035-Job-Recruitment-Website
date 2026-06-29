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

    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final RecruiterRepo recruiterRepo;
    private final CompanyRepo companyRepo;
    private final CVRepo cvRepo;
    private final FileStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder;

    private User getCurrentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Bạn chưa đăng nhập");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy người dùng"));
    }

    @GetMapping
    public String viewProfile(Authentication auth, Model model) {
        try {
            User user = getCurrentUser(auth);
            if (user.getRole() == UserRole.CANDIDATE) {
                Candidate candidate = candidateRepository.findByUser_UserId(user.getUserId())
                        .orElseThrow(() -> new IllegalStateException("Không tìm thấy hồ sơ ứng viên"));
                model.addAttribute("profile", candidate);
                model.addAttribute("cvList", candidate.getCvList());
                return "profile/seeker";
            } else if (user.getRole() == UserRole.RECRUITER) {
                Recruiter recruiter = recruiterRepo.findByUser_UserId(user.getUserId())
                        .orElseThrow(() -> new IllegalStateException("Không tìm thấy hồ sơ nhà tuyển dụng"));
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
            Candidate candidate = candidateRepository.findByUser_UserId(user.getUserId())
                    .orElseThrow(() -> new IllegalStateException("Không tìm thấy hồ sơ"));

            candidate.setFullName(fullName);
            candidate.setPhone(phone);
            candidate.setHeadline(headline);
            candidate.setSummary(summary);
            candidate.setSkills(skills);
            candidate.setAddress(address);

            candidateRepository.save(candidate);

            if (cvFile != null && !cvFile.isEmpty()) {
                String uploadedPath = fileStorageService.uploadFile(cvFile, "cvs");
                // Reset defaults
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

            redirectAttributes.addFlashAttribute("success", "Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
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
                    .orElseThrow(() -> new IllegalStateException("Không tìm thấy hồ sơ"));

            recruiter.setFullName(fullName);
            recruiter.setPhone(phone);
            recruiter.setPosition(position);

            Company company = companyRepo.findById(companyId)
                    .orElseThrow(() -> new IllegalArgumentException("Công ty không hợp lệ"));
            
            // If they are updating their company name, website, address etc.
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

            redirectAttributes.addFlashAttribute("success", "Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
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
                redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không chính xác!");
                return "redirect:/profile#change-password";
            }

            if (newPassword == null || newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự!");
                return "redirect:/profile#change-password";
            }

            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp!");
                return "redirect:/profile#change-password";
            }

            user.setPasswordHash(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/profile";
    }
}
