package com.se2035.jrw.service;

import com.se2035.jrw.dto.JobRequest;
import com.se2035.jrw.entity.Company;
import com.se2035.jrw.entity.Job;
import com.se2035.jrw.entity.Recruiter;
import com.se2035.jrw.entity.User;
import com.se2035.jrw.enums.JobStatus;
import com.se2035.jrw.enums.UserRole;
import com.se2035.jrw.enums.UserStatus;
import com.se2035.jrw.exception.BadRequestException;
import com.se2035.jrw.exception.ResourceNotFoundException;
import com.se2035.jrw.entity.Industry;
import com.se2035.jrw.repository.CompanyRepo;
import com.se2035.jrw.repository.IndustryRepo;
import com.se2035.jrw.repository.JobRepo;
import com.se2035.jrw.repository.RecruiterRepo;
import com.se2035.jrw.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {
    private final JobRepo jobRepo;
    private final CompanyRepo companyRepo;
    private final RecruiterRepo recruiterRepo;
    private final UserRepo userRepo;
    private final IndustryRepo industryRepo;

    @Override
    @Transactional
    public List<Job> findAll() {
        return jobRepo.findAll();
    }

    @Override
    @Transactional
    public Job createJob(JobRequest req) {
        Company company = companyRepo.findById(req.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        Recruiter recruiter = recruiterRepo.findById(req.getRecruiterId())
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));
        Industry industry = industryRepo.findById(req.getIndustryId())
                .orElseThrow(() -> new ResourceNotFoundException("Industry not found"));

        if (!recruiter.getCompany().getCompanyId().equals(company.getCompanyId())) {
            throw new BadRequestException("Recruiter must belong to company");
        }
        if (req.getSalaryMin() != null && req.getSalaryMax() != null
                && req.getSalaryMin().compareTo(req.getSalaryMax()) > 0) {
            throw new BadRequestException("Salary min > max");
        }

        Job job = Job.builder()
                .company(company)
                .recruiter(recruiter)
                .industry(industry)
                .title(req.getTitle())
                .description(req.getDescription())
                .requirement(req.getRequirement())
                .benefit(req.getBenefit())
                .location(req.getLocation())
                .salaryMin(req.getSalaryMin())
                .salaryMax(req.getSalaryMax())
                .employmentType(req.getEmploymentType())
                .experienceRequired(req.getExperienceRequired())
                .requiredSkills(req.getRequiredSkills())
                .deadline(req.getDeadline())
                .status(JobStatus.PENDING)
                .build();

        return jobRepo.save(job);
    }

    @Override
    @Transactional
    public Job editJob(Integer jobId, JobRequest req) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (job.getStatus() != JobStatus.PENDING && job.getStatus() != JobStatus.REJECTED) {
            throw new BadRequestException("Only unapproved and undeleted jobs can be edited. " +
                    "Current status: " + job.getStatus());
        }
        if (req.getSalaryMin() != null && req.getSalaryMax() != null
                && req.getSalaryMin().compareTo(req.getSalaryMax()) > 0) {
            throw new BadRequestException("Salary min > max");
        }
        Industry industry = industryRepo.findById(req.getIndustryId())
                .orElseThrow(() -> new ResourceNotFoundException("Industry not found"));

        job.setTitle(req.getTitle());
        job.setIndustry(industry);
        job.setDescription(req.getDescription());
        job.setRequirement(req.getRequirement());
        job.setBenefit(req.getBenefit());
        job.setLocation(req.getLocation());
        job.setSalaryMin(req.getSalaryMin());
        job.setSalaryMax(req.getSalaryMax());
        job.setEmploymentType(req.getEmploymentType());
        job.setExperienceRequired(req.getExperienceRequired());
        job.setRequiredSkills(req.getRequiredSkills());
        job.setDeadline(req.getDeadline());
        job.setStatus(JobStatus.PENDING);
        job.setApprovedBy(null);
        job.setApprovedAt(null);

        return jobRepo.save(job);
    }

    @Override
    @Transactional
    public void deleteJob(Integer jobId) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        job.setStatus(JobStatus.DELETED);

        jobRepo.save(job);
    }

    @Override
    public Job closeJob(Integer jobId) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        job.setStatus(JobStatus.CLOSED);

        return jobRepo.save(job);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Job> findPendingJobs(Pageable pageable) {
        return jobRepo.findByStatus(JobStatus.PENDING, pageable);
    }

    @Override
    @Transactional
    public Job approveJob(Integer jobId, User adminUser) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (job.getStatus() != JobStatus.PENDING) {
            throw new BadRequestException("Only jobs with PENDING status can be approved");
        }

        job.setStatus(JobStatus.APPROVED);
        job.setApprovedBy(adminUser);
        job.setApprovedAt(LocalDateTime.now());

        return jobRepo.save(job);
    }

    @Override
    @Transactional
    public Job rejectJob(Integer jobId, User adminUser) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (job.getStatus() != JobStatus.PENDING) {
            throw new BadRequestException("Only jobs with PENDING status can be rejected");
        }

        job.setStatus(JobStatus.REJECTED);
        job.setApprovedBy(adminUser);
        job.setApprovedAt(LocalDateTime.now());

        return jobRepo.save(job);
    }
}
