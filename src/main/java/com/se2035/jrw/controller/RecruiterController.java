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

    private final UserRepository userRepository;
    private final RecruiterRepo recruiterRepo;
    private final JobRepository jobRepository;
    private final IndustryRepo industryRepo;
    private final ApplicationRepository applicationRepository;

    private Recruiter getCurrentRecruiter(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Bạn chưa đăng nhập");
        }
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .flatMap(u -> recruiterRepo.findByUser_UserId(u.getUserId()))
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy thông tin nhà tuyển dụng"));
    }

    // Recruiter Dashboard
    @GetMapping("/jobs/manage")
    public String manageJobs(Authentication auth, Model model) {
        try {
            Recruiter recruiter = getCurrentRecruiter(auth);
            List<Job> myJobs = jobRepository.findAll().stream()
                    .filter(j -> j.getRecruiter().getRecruiterId().equals(recruiter.getRecruiterId()) 
                            && j.getStatus() != JobStatus.DELETED)
                    .toList();
            model.addAttribute("myJobs", myJobs);
            return "recruiter/dashboard";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    // Show Create Job Form
    @GetMapping("/jobs/create")
    public String showCreateForm(Authentication auth, Model model) {
        try {
            getCurrentRecruiter(auth);
            model.addAttribute("job", new Job());
            model.addAttribute("industries", industryRepo.findAll());
            return "recruiter/job-form";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    // Process Create Job
    @PostMapping("/jobs/create")
    public String createJob(
            @RequestParam String title,
            @RequestParam Integer industryId,
            @RequestParam String location,
            @RequestParam BigDecimal salaryMin,
            @RequestParam BigDecimal salaryMax,
            @RequestParam String employmentType,
            @RequestParam Integer experienceRequired,
            @RequestParam String requiredSkills,
            @RequestParam String deadlineStr,
            @RequestParam String description,
            @RequestParam String requirement,
            @RequestParam String benefit,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        try {
            Recruiter recruiter = getCurrentRecruiter(auth);
            Industry industry = industryRepo.findById(industryId)
                    .orElseThrow(() -> new IllegalArgumentException("Ngành nghề không hợp lệ"));

            if (salaryMin != null && salaryMax != null && salaryMin.compareTo(salaryMax) > 0) {
                throw new IllegalArgumentException("Minimum salary cannot be greater than maximum salary");
            }

            Job job = Job.builder()
                    .title(title)
                    .recruiter(recruiter)
                    .company(recruiter.getCompany())
                    .industry(industry)
                    .location(location)
                    .salaryMin(salaryMin)
                    .salaryMax(salaryMax)
                    .employmentType(employmentType)
                    .experienceRequired(experienceRequired)
                    .requiredSkills(requiredSkills)
                    .deadline(deadlineStr.isEmpty() ? null : LocalDate.parse(deadlineStr))
                    .description(description)
                    .requirement(requirement)
                    .benefit(benefit)
                    .status(JobStatus.PENDING)
                    .build();

            jobRepository.save(job);
            redirectAttributes.addFlashAttribute("success", "Đã tạo tin tuyển dụng thành công! Vui lòng chờ kiểm duyệt.");
            return "redirect:/jobs/manage";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/jobs/create";
        }
    }

    // Show Edit Job Form
    @GetMapping("/jobs/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Authentication auth, Model model) {
        try {
            Recruiter recruiter = getCurrentRecruiter(auth);
            Job job = jobRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tin tuyển dụng"));

            if (!job.getRecruiter().getRecruiterId().equals(recruiter.getRecruiterId())) {
                throw new IllegalStateException("Bạn không có quyền sửa tin này");
            }

            model.addAttribute("job", job);
            model.addAttribute("industries", industryRepo.findAll());
            return "recruiter/job-form";
        } catch (Exception e) {
            return "redirect:/jobs/manage";
        }
    }

    // Process Edit Job
    @PostMapping("/jobs/edit")
    public String editJob(
            @RequestParam Integer jobId,
            @RequestParam String title,
            @RequestParam Integer industryId,
            @RequestParam String location,
            @RequestParam BigDecimal salaryMin,
            @RequestParam BigDecimal salaryMax,
            @RequestParam String employmentType,
            @RequestParam Integer experienceRequired,
            @RequestParam String requiredSkills,
            @RequestParam String deadlineStr,
            @RequestParam String description,
            @RequestParam String requirement,
            @RequestParam String benefit,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        try {
            Recruiter recruiter = getCurrentRecruiter(auth);
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tin tuyển dụng"));

            if (!job.getRecruiter().getRecruiterId().equals(recruiter.getRecruiterId())) {
                throw new IllegalStateException("Bạn không có quyền sửa tin này");
            }

            Industry industry = industryRepo.findById(industryId)
                    .orElseThrow(() -> new IllegalArgumentException("Ngành nghề không hợp lệ"));

            if (salaryMin != null && salaryMax != null && salaryMin.compareTo(salaryMax) > 0) {
                throw new IllegalArgumentException("Minimum salary cannot be greater than maximum salary");
            }

            job.setTitle(title);
            job.setIndustry(industry);
            job.setLocation(location);
            job.setSalaryMin(salaryMin);
            job.setSalaryMax(salaryMax);
            job.setEmploymentType(employmentType);
            job.setExperienceRequired(experienceRequired);
            job.setRequiredSkills(requiredSkills);
            job.setDeadline(deadlineStr.isEmpty() ? null : LocalDate.parse(deadlineStr));
            job.setDescription(description);
            job.setRequirement(requirement);
            job.setBenefit(benefit);
            job.setStatus(JobStatus.PENDING); // return to pending for re-approval

            jobRepository.save(job);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thành công! Tin tuyển dụng sẽ được kiểm duyệt lại.");
            return "redirect:/jobs/manage";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/jobs/edit/" + jobId;
        }
    }

    // Process Close Job
    @PostMapping("/jobs/close")
    public String closeJob(@RequestParam Integer id, Authentication auth, RedirectAttributes redirectAttributes) {
        try {
            Recruiter recruiter = getCurrentRecruiter(auth);
            Job job = jobRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tin tuyển dụng"));

            if (!job.getRecruiter().getRecruiterId().equals(recruiter.getRecruiterId())) {
                throw new IllegalStateException("Bạn không có quyền thực hiện thao tác này");
            }

            job.setStatus(JobStatus.CLOSED);
            jobRepository.save(job);
            redirectAttributes.addFlashAttribute("success", "Đã đóng tin tuyển dụng thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/jobs/manage";
    }

    // Process Delete Job
    @PostMapping("/jobs/delete")
    public String deleteJob(@RequestParam Integer id, Authentication auth, RedirectAttributes redirectAttributes) {
        try {
            Recruiter recruiter = getCurrentRecruiter(auth);
            Job job = jobRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tin tuyển dụng"));

            if (!job.getRecruiter().getRecruiterId().equals(recruiter.getRecruiterId())) {
                throw new IllegalStateException("Bạn không có quyền thực hiện thao tác này");
            }

            job.setStatus(JobStatus.DELETED);
            jobRepository.save(job);
            redirectAttributes.addFlashAttribute("success", "Đã xóa tin tuyển dụng thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/jobs/manage";
    }

    // Manage Applications
    @GetMapping("/applications/manage")
    public String manageApplications(Authentication auth, Model model) {
        try {
            Recruiter recruiter = getCurrentRecruiter(auth);
            List<Application> applications = applicationRepository
                    .findByJob_Recruiter_RecruiterIdOrderByAppliedAtDesc(recruiter.getRecruiterId());
            model.addAttribute("applications", applications);
            return "recruiter/applications";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    // View Applications by specific Job
    @GetMapping("/applications/job/{jobId}")
    public String applicationsByJob(@PathVariable Integer jobId, Authentication auth, Model model) {
        try {
            Recruiter recruiter = getCurrentRecruiter(auth);
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tin tuyển dụng"));

            if (!job.getRecruiter().getRecruiterId().equals(recruiter.getRecruiterId())) {
                throw new IllegalStateException("Bạn không có quyền xem đơn ứng tuyển của tin này");
            }

            List<Application> applications = applicationRepository.findByJob_JobIdOrderByAppliedAtDesc(jobId);
            model.addAttribute("applications", applications);
            model.addAttribute("jobId", jobId);
            return "recruiter/applications";
        } catch (Exception e) {
            return "redirect:/jobs/manage";
        }
    }

    // Process Update Application Status
    @PostMapping("/applications/status")
    public String updateApplicationStatus(
            @RequestParam Integer applicationId,
            @RequestParam String status,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        try {
            Recruiter recruiter = getCurrentRecruiter(auth);
            Application app = applicationRepository.findById(applicationId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn ứng tuyển"));

            if (!app.getJob().getRecruiter().getRecruiterId().equals(recruiter.getRecruiterId())) {
                throw new IllegalStateException("Bạn không có quyền duyệt hồ sơ này");
            }

            ApplicationStatus appStatus = ApplicationStatus.valueOf(status.toUpperCase());
            app.setStatus(appStatus);
            applicationRepository.save(app);

            redirectAttributes.addFlashAttribute("success", "Đã cập nhật trạng thái đơn ứng tuyển.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/applications/manage";
    }
}
