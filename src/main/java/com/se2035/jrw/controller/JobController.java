package com.se2035.jrw.controller;

import com.se2035.jrw.config.SecurityUtils;
import com.se2035.jrw.entity.Job;
import com.se2035.jrw.enums.JobStatus;
import com.se2035.jrw.repository.CandidateRepo;
import com.se2035.jrw.repository.UserRepo;
import com.se2035.jrw.service.ApplicationService;
import com.se2035.jrw.service.CVService;
import com.se2035.jrw.service.JobService;
import com.se2035.jrw.service.SavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final SavedJobService savedJobService;
    private final ApplicationService applicationService;
    private final CVService cvService;
    private final SecurityUtils securityUtils;
    private final UserRepo userRepo;
    private final CandidateRepo candidateRepo;

    @GetMapping
    public String listJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String employmentType,
            @RequestParam(required = false) String industry,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {

        page = Math.max(page, 0);
        size = Math.max(1, Math.min(size, 100));

        Page<Job> jobPage = jobService.searchApprovedJobs(
                keyword,
                location,
                employmentType,
                industry,
                PageRequest.of(page, size)
        );

        model.addAttribute("jobs", jobPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobPage.getTotalPages());
        model.addAttribute("totalElements", jobPage.getTotalElements());
        model.addAttribute("size", size);

        model.addAttribute(
                "locations",
                jobService.findDistinctLocations()
        );

        model.addAttribute(
                "employmentTypes",
                jobService.findDistinctEmploymentTypes()
        );

        model.addAttribute(
                "industries",
                jobService.findDistinctIndustries()
        );

        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        model.addAttribute("employmentType", employmentType);
        model.addAttribute("industry", industry);

        return "job-list";
    }

    @GetMapping("/{id}")
    public String showJobDetail(
            @PathVariable("id") Integer id,
            Authentication auth,
            Model model) {

        Job job = jobService.findJobDetailWithAssociations(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Job not found")
                );

        if (job.getStatus() != JobStatus.APPROVED) {
            boolean canView = false;

            if (auth != null && auth.isAuthenticated()) {
                boolean isAdmin = auth.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN")
                        );

                if (isAdmin) {
                    canView = true;
                } else {
                    String email = auth.getName();

                    if (job.getRecruiter() != null
                            && job.getRecruiter().getUser() != null
                            && email.equalsIgnoreCase(
                                    job.getRecruiter()
                                            .getUser()
                                            .getEmail()
                            )) {

                        canView = true;
                    }
                }
            }

            if (!canView) {
                throw new IllegalArgumentException(
                        "Job not found or not approved yet"
                );
            }
        }

        model.addAttribute("job", job);

        List<Job> similarJobs = jobService.searchApprovedJobs(
                        null,
                        null,
                        null,
                        job.getIndustry() != null
                                ? job.getIndustry().getIndustryName()
                                : null,
                        PageRequest.of(0, 4)
                )
                .getContent()
                .stream()
                .filter(similarJob ->
                        !similarJob.getJobId().equals(id)
                )
                .limit(3)
                .toList();

        model.addAttribute("similarJobs", similarJobs);

        if (auth != null && auth.isAuthenticated()) {
            String email = auth.getName();

            userRepo.findByEmail(email)
                    .ifPresent(user ->
                            candidateRepo
                                    .findByUser_UserId(
                                            user.getUserId()
                                    )
                                    .ifPresent(candidate -> {
                                        Integer candidateId =
                                                candidate.getCandidateId();

                                        model.addAttribute(
                                                "hasApplied",
                                                applicationService.hasApplied(
                                                        candidateId,
                                                        id
                                                )
                                        );

                                        model.addAttribute(
                                                "isSaved",
                                                savedJobService.isSaved(
                                                        candidateId,
                                                        id
                                                )
                                        );

                                        model.addAttribute(
                                                "cvList",
                                                cvService
                                                        .getCurrentCv(
                                                                candidateId
                                                        )
                                                        .map(List::of)
                                                        .orElseGet(List::of)
                                        );
                                    })
                    );
        }

        return "job-detail";
    }

    @PostMapping("/{id}/apply")
    public String applyJob(
            @PathVariable("id") Integer id,
            @RequestParam("cvId") Integer cvId,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        try {
            Integer candidateId =
                    securityUtils.getCandidateId(auth);

            applicationService.apply(
                    candidateId,
                    id,
                    cvId
            );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Applied successfully!"
            );

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/jobs/" + id;
    }

    @PostMapping("/{id}/save")
    public String toggleSaveJob(
            @PathVariable("id") Integer id,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        try {
            Integer candidateId =
                    securityUtils.getCandidateId(auth);

            if (savedJobService.isSaved(candidateId, id)) {
                savedJobService.unsaveJob(
                        candidateId,
                        id
                );

                redirectAttributes.addFlashAttribute(
                        "success",
                        "Job unsaved successfully."
                );

            } else {
                savedJobService.saveJob(
                        candidateId,
                        id
                );

                redirectAttributes.addFlashAttribute(
                        "success",
                        "Job saved successfully."
                );
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/jobs/" + id;
    }
}