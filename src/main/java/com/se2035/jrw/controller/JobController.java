package com.se2035.jrw.controller;

import com.se2035.jrw.config.SecurityUtils;
import com.se2035.jrw.repository.CVRepository;
import com.se2035.jrw.repository.CandidateRepository;
import com.se2035.jrw.repository.UserRepository;
import com.se2035.jrw.service.ApplicationService;
import com.se2035.jrw.service.JobService;
import com.se2035.jrw.service.SavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final ApplicationService applicationService;
    private final SavedJobService savedJobService;
    private final SecurityUtils securityUtils;
    private final CVRepository cvRepository;
    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;

    @GetMapping("/jobs")
    public String listJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String employmentType,
            @RequestParam(required = false) String industry,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        var jobPage = jobService.searchJobs(keyword, location, employmentType, industry, pageable);

        model.addAttribute("jobs", jobPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        model.addAttribute("employmentType", employmentType);
        model.addAttribute("industry", industry);
        model.addAttribute("locations", jobService.getDistinctLocations());
        model.addAttribute("employmentTypes", jobService.getDistinctEmploymentTypes());
        model.addAttribute("industries", jobService.getDistinctIndustries());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobPage.getTotalPages());

        return "job-list";
    }

    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable Integer id, Model model, Authentication auth) {
        var job = jobService.getApprovedJobById(id);
        model.addAttribute("job", job);

        if (auth != null && auth.isAuthenticated()) {
            Integer candidateId = securityUtils.getCandidateId(auth);
            model.addAttribute("hasApplied", applicationService.hasApplied(candidateId, id));
            model.addAttribute("isSaved", savedJobService.isSaved(candidateId, id));

            var candidate = candidateRepository.findById(candidateId).orElse(null);
            if (candidate != null) {
                model.addAttribute("cvList", cvRepository.findByCandidate(candidate));
            }
        }

        return "job-detail";
    }

    @PostMapping("/jobs/{id}/apply")
    public String apply(@PathVariable Integer id,
                        @RequestParam Integer cvId,
                        Authentication auth,
                        RedirectAttributes redirectAttributes) {
        try {
            Integer candidateId = securityUtils.getCandidateId(auth);
            applicationService.apply(candidateId, id, cvId);
            redirectAttributes.addFlashAttribute("success", "Nộp hồ sơ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/jobs/" + id;
    }

    @PostMapping("/jobs/{id}/save")
    public String saveJob(@PathVariable Integer id,
                          Authentication auth,
                          RedirectAttributes redirectAttributes) {
        try {
            Integer candidateId = securityUtils.getCandidateId(auth);
            if (savedJobService.isSaved(candidateId, id)) {
                savedJobService.unsaveJob(candidateId, id);
                redirectAttributes.addFlashAttribute("success", "Đã bỏ lưu tin tuyển dụng");
            } else {
                savedJobService.saveJob(candidateId, id);
                redirectAttributes.addFlashAttribute("success", "Đã lưu tin tuyển dụng");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/jobs/" + id;
    }
}
