package com.se2035.jrw.controller;

import com.se2035.jrw.entity.*;
import com.se2035.jrw.enums.ApplicationStatus;
import com.se2035.jrw.enums.JobStatus;
import com.se2035.jrw.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecruiterController {

    private final UserRepo userRepo;
    private final RecruiterRepo recruiterRepo;
    private final JobRepo jobRepo;
    private final IndustryRepo industryRepo;
    private final ApplicationRepo applicationRepo;

    private Recruiter getCurrentRecruiter(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("You are not logged in");
        }
        String email = auth.getName();
        return userRepo.findByEmail(email)
                .flatMap(u -> recruiterRepo.findByUser_UserId(u.getUserId()))
                .orElseThrow(() -> new IllegalStateException("Recruiter profile not found"));
    }

    @GetMapping("/jobs/manage")
    public String manageJobs(Authentication auth, Model model) {
        try {
            Recruiter recruiter = getCurrentRecruiter(auth);
            List<Job> myJobs = jobRepo.findAll().stream()
                    .filter(j -> j.getRecruiter().getRecruiterId().equals(recruiter.getRecruiterId()) 
                              && j.getStatus() != JobStatus.DELETED)
                    .toList();
            model.addAttribute("jobs", myJobs);
            return "recruiter/dashboard";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @GetMapping("/jobs/create")
    public String showCreateForm(Authentication auth, Model model) {
        try {
            Recruiter recruiter = getCurrentRecruiter(auth);
            Job job = new Job();
            job.setCompany(recruiter.getCompany());
            model.addAttribute("job", job);
            model.addAttribute("industries", industryRepo.findAll());
            return "recruiter/job-form";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @PostMapping("/jobs/create")
    public String createJob(
            @ModelAttribute("job") Job job,
            @RequestParam("industryId") Integer industryId,
            Authentication auth,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            Recruiter recruiter = getCurrentRecruiter(auth);
            job.setRecruiter(recruiter);
            job.setCompany(recruiter.getCompany());

            Industry ind = industryRepo.findById(industryId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid industry"));
            job.setIndustry(ind);

            if (job.getSalaryMin() != null && job.getSalaryMax() != null 
                    && job.getSalaryMin().compareTo(job.getSalaryMax()) > 0) {
                model.addAttribute("errorMessage", "Salary Min must be less than or equal to Salary Max");
                model.addAttribute("industries", industryRepo.findAll());
                return "recruiter/job-form";
            }

            job.setStatus(JobStatus.PENDING);
            jobRepo.save(job);
            redirectAttributes.addFlashAttribute("success", "Job posting created successfully! Pending admin approval.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/jobs/manage";
    }

    @GetMapping("/jobs/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Authentication auth, Model model) {
        try {
            Recruiter recruiter = getCurrentRecruiter(auth);
            Job job = jobRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Job not found"));

            if (!job.getRecruiter().getRecruiterId().equals(recruiter.getRecruiterId())) {
                throw new IllegalStateException("You do not have permission to edit this job");
            }

            model.addAttribute("job", job);
            model.addAttribute("industries", industryRepo.findAll());
            return "recruiter/job-form";
        } catch (Exception e) {
            return "redirect:/jobs/manage";
        }
    }

    @PostMapping("/jobs/edit/{jobId}")
    public String editJob(
            @PathVariable Integer jobId,
            @ModelAttribute("job") Job jobForm,
            @RequestParam("industryId") Integer industryId,
            Authentication auth,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            Recruiter recruiter = getCurrentRecruiter(auth);
            Job job = jobRepo.findById(jobId)
                    .orElseThrow(() -> new IllegalArgumentException("Job not found"));

            if (!job.getRecruiter().getRecruiterId().equals(recruiter.getRecruiterId())) {
                throw new IllegalStateException("You do not have permission to edit this job");
            }

            Industry ind = industryRepo.findById(industryId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid industry"));

            if (jobForm.getSalaryMin() != null && jobForm.getSalaryMax() != null 
                    && jobForm.getSalaryMin().compareTo(jobForm.getSalaryMax()) > 0) {
                model.addAttribute("errorMessage", "Salary Min must be less than or equal to Salary Max");
                model.addAttribute("industries", industryRepo.findAll());
                return "recruiter/job-form";
            }

            job.setTitle(jobForm.getTitle());
            job.setIndustry(ind);
            job.setDescription(jobForm.getDescription());
            job.setRequirement(jobForm.getRequirement());
            job.setBenefit(jobForm.getBenefit());
            job.setLocation(jobForm.getLocation());
            job.setSalaryMin(jobForm.getSalaryMin());
            job.setSalaryMax(jobForm.getSalaryMax());
            job.setEmploymentType(jobForm.getEmploymentType());
            job.setExperienceRequired(jobForm.getExperienceRequired());
            job.setRequiredSkills(jobForm.getRequiredSkills());
            job.setDeadline(jobForm.getDeadline());
            
            job.setStatus(JobStatus.PENDING);
            jobRepo.save(job);

            redirectAttributes.addFlashAttribute("success", "Job updated successfully! It will be reviewed by admin again.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/jobs/manage";
    }

    @PostMapping("/jobs/close/{id}")
    public String closeJob(@PathVariable Integer id, Authentication auth, RedirectAttributes redirectAttributes) {
        try {
            Recruiter recruiter = getCurrentRecruiter(auth);
            Job job = jobRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Job not found"));

            if (!job.getRecruiter().getRecruiterId().equals(recruiter.getRecruiterId())) {
                throw new IllegalStateException("You do not have permission to perform this action");
            }

            job.setStatus(JobStatus.CLOSED);
            jobRepo.save(job);
            redirectAttributes.addFlashAttribute("success", "Job closed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/jobs/manage";
    }

    @PostMapping("/jobs/delete/{id}")
    public String deleteJob(@PathVariable Integer id, Authentication auth, RedirectAttributes redirectAttributes) {
        try {
            Recruiter recruiter = getCurrentRecruiter(auth);
            Job job = jobRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Job not found"));

            if (!job.getRecruiter().getRecruiterId().equals(recruiter.getRecruiterId())) {
                throw new IllegalStateException("You do not have permission to perform this action");
            }

            job.setStatus(JobStatus.DELETED);
            jobRepo.save(job);
            redirectAttributes.addFlashAttribute("success", "Job deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/jobs/manage";
    }

    @GetMapping("/applications/manage")
    public String manageApplications(Authentication auth, Model model) {
        try {
            Recruiter recruiter = getCurrentRecruiter(auth);
            List<Application> applications = applicationRepo
                    .findByJob_Recruiter_RecruiterIdOrderByAppliedAtDesc(recruiter.getRecruiterId());
            model.addAttribute("applications", applications);
            return "recruiter/applications";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @GetMapping("/applications/job/{jobId}")
    public String applicationsByJob(@PathVariable Integer jobId, Authentication auth, Model model) {
        try {
            Recruiter recruiter = getCurrentRecruiter(auth);
            Job job = jobRepo.findById(jobId)
                    .orElseThrow(() -> new IllegalArgumentException("Job not found"));

            if (!job.getRecruiter().getRecruiterId().equals(recruiter.getRecruiterId())) {
                throw new IllegalStateException("You do not have permission to view applications for this job");
            }

            List<Application> applications = applicationRepo.findByJob_JobIdOrderByAppliedAtDesc(jobId);
            model.addAttribute("applications", applications);
            model.addAttribute("jobId", jobId);
            return "recruiter/applications";
        } catch (Exception e) {
            return "redirect:/jobs/manage";
        }
    }

    @PostMapping("/applications/status")
    public String updateApplicationStatus(
            @RequestParam Integer applicationId,
            @RequestParam String status,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        try {
            Recruiter recruiter = getCurrentRecruiter(auth);
            Application app = applicationRepo.findById(applicationId)
                    .orElseThrow(() -> new IllegalArgumentException("Application not found"));

            if (!app.getJob().getRecruiter().getRecruiterId().equals(recruiter.getRecruiterId())) {
                throw new IllegalStateException("You do not have permission to review this application");
            }

            ApplicationStatus appStatus = ApplicationStatus.valueOf(status.toUpperCase());
            app.setStatus(appStatus);
            applicationRepo.save(app);

            redirectAttributes.addFlashAttribute("success", "Application status updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/applications/manage";
    }
}
