package com.se2035.jrw.controller;

import com.se2035.jrw.entity.Job;
import com.se2035.jrw.entity.User;
import com.se2035.jrw.repository.UserRepo;
import com.se2035.jrw.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/jobs")
@RequiredArgsConstructor
public class AdminJobController {

    private final JobService jobService;
    private final UserRepo userRepo;
    private User getCurrentAdmin(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("You are not logged in");
        }
        return userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Admin not found"));
    }

    @GetMapping("/pending")
    public String listPendingJobs(
            @RequestParam(value = "status", required = false) com.se2035.jrw.enums.JobStatus status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {

        com.se2035.jrw.enums.JobStatus selectedStatus = (status != null) ? status : com.se2035.jrw.enums.JobStatus.PENDING;

        Page<Job> jobPage = jobService.findJobsByStatus(
                selectedStatus,
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );

        model.addAttribute("jobs", jobPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobPage.getTotalPages());
        model.addAttribute("totalItems", jobPage.getTotalElements());
        model.addAttribute("selectedStatus", selectedStatus);

        return "admin/job/pending-list";
    }

    @PostMapping("/approve/{id}")
    public String approveJob(
            @PathVariable("id") Integer id,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            User admin = getCurrentAdmin(auth);
            jobService.approveJob(id, admin);
            redirectAttributes.addFlashAttribute("success", "Job approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Approval failed: " + e.getMessage());
        }
        return "redirect:/admin/jobs/pending";
    }

    @PostMapping("/reject/{id}")
    public String rejectJob(
            @PathVariable("id") Integer id,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            User admin = getCurrentAdmin(auth);
            jobService.rejectJob(id, admin);
            redirectAttributes.addFlashAttribute("success", "Job rejected successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Rejection failed: " + e.getMessage());
        }
        return "redirect:/admin/jobs/pending";
    }
}