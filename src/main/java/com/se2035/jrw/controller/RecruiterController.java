package com.se2035.jrw.controller;

import com.se2035.jrw.dto.JobRequest;
import com.se2035.jrw.entity.*;
import com.se2035.jrw.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/recruiter")
public class RecruiterController {

    private final RecruiterService recruiterService;
    private final JobService jobService;
    private final IndustryService industryService;
    private final ApplicationService applicationService;

    private Recruiter getCurrentRecruiter(Authentication auth) {
        return recruiterService.getCurrentRecruiter(auth);
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Recruiter recruiter = getCurrentRecruiter(auth);
        model.addAttribute("recruiter", recruiter);
        model.addAllAttributes(recruiterService.getDashboardStats(recruiter));
        return "recruiter/dashboard";
    }

    @GetMapping("/jobs")
    public String manageJobs(Authentication auth, Model model) {
        Recruiter recruiter = getCurrentRecruiter(auth);
        List<Job> myJobs = jobService.getMyJobs(recruiter);
        model.addAttribute("jobs", myJobs);
        return "recruiter/jobs";
    }

    @GetMapping("/jobs/create")
    public String showCreateForm(Authentication auth, Model model) {
            Recruiter recruiter = getCurrentRecruiter(auth);
            JobRequest request = new JobRequest();
            request.setRecruiterId(recruiter.getRecruiterId());
            request.setCompanyId(recruiter.getCompany().getCompanyId());
            model.addAttribute("job", request);
            model.addAttribute("industries", industryService.getAllIndustries());
            return "recruiter/job-form";
    }

    @PostMapping("/jobs/create")
    public String createJob(
            @ModelAttribute("job") JobRequest request,
            @RequestParam("industryId") Integer industryId,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

            Recruiter recruiter = getCurrentRecruiter(auth);
            request.setRecruiterId(recruiter.getRecruiterId());
            request.setCompanyId(recruiter.getCompany().getCompanyId());

            jobService.createJob(request);

        redirectAttributes.addFlashAttribute(
                "success",
                "Job posted successfully. Waiting for admin approval.");
        return "redirect:/recruiter/jobs";
    }

    @GetMapping("/jobs/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Authentication auth, Model model) {
            Recruiter recruiter = getCurrentRecruiter(auth);
            Job job = jobService.findById(id); 

            if (!job.getRecruiter().getRecruiterId().equals(recruiter.getRecruiterId())) {
                throw new IllegalStateException("You do not have permission to edit this job");
            }

            model.addAttribute("job", job);
            model.addAttribute("industries", industryService.getAllIndustries());

            return "recruiter/job-form";
    }

    @PostMapping("/jobs/edit/{id}")
    public String editJob(
            @PathVariable Integer id,
            @ModelAttribute("job") JobRequest request,
            Authentication auth,
            RedirectAttributes redirectAttributes,
            Model model) {

            Recruiter recruiter = getCurrentRecruiter(auth);
            request.setRecruiterId(recruiter.getRecruiterId());
            request.setCompanyId(recruiter.getCompany().getCompanyId());

            jobService.editJob(id, request);

            redirectAttributes.addFlashAttribute("success", "Job updated successfully! It will be reviewed by admin again.");
        return "redirect:/recruiter/jobs";
    }

    @PostMapping("/jobs/close/{id}")
    public String closeJob(@PathVariable Integer id, Authentication auth, RedirectAttributes redirectAttributes) {
            jobService.closeJob(id);
            redirectAttributes.addFlashAttribute("success", "Job closed successfully.");
        return "redirect:/recruiter/jobs";
    }

    @PostMapping("/jobs/delete/{id}")
    public String deleteJob(@PathVariable Integer id, Authentication auth, RedirectAttributes redirectAttributes) {
            jobService.deleteJob(id);
            redirectAttributes.addFlashAttribute("success", "Job deleted successfully.");
        return "redirect:/recruiter/jobs";
    }

    @GetMapping("/applications")
    public String manageApplications(Authentication auth, Model model) {
            Recruiter recruiter = getCurrentRecruiter(auth);
            List<Application> applications = applicationService.findByRecruiterId(recruiter.getRecruiterId());
            model.addAttribute("applications", applications);
            return "recruiter/applications";
    }

    @GetMapping("/applications/job/{jobId}")
    public String applicationsByJob(@PathVariable Integer jobId, Authentication auth, Model model) {
            Recruiter recruiter = getCurrentRecruiter(auth);
            Job job = jobService.findById(jobId);

            if (!job.getRecruiter().getRecruiterId().equals(recruiter.getRecruiterId())) {
                throw new IllegalStateException("You do not have permission to view applications for this job");
            }

            List<Application> applications = applicationService.findByJobId(jobId);
            model.addAttribute("applications", applications);
            model.addAttribute("jobId", jobId);
            return "recruiter/applications";
    }

    @PostMapping("/applications/status")
    public String updateApplicationStatus(@RequestParam Integer applicationId,
                                          @RequestParam String status,
                                          Authentication auth,
                                          RedirectAttributes redirectAttributes) {
        Recruiter recruiter = getCurrentRecruiter(auth);
        switch (status.toUpperCase()) {
            case "SHORTLISTED" -> applicationService.shortlist(applicationId, recruiter.getRecruiterId());
            case "REJECTED" -> applicationService.reject(applicationId, recruiter.getRecruiterId());
            case "HIRED" -> applicationService.hire(applicationId, recruiter.getRecruiterId());
            default -> {
                redirectAttributes.addFlashAttribute("error", "Invalid status value.");
                return "redirect:/recruiter/applications";
            }
        }
        redirectAttributes.addFlashAttribute("success", "Application status updated successfully.");
        return "redirect:/recruiter/applications";
    }
}
