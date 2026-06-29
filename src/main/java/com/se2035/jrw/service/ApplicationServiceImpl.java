package com.se2035.jrw.service;

import com.se2035.jrw.entity.Application;
import com.se2035.jrw.entity.CV;
import com.se2035.jrw.entity.Candidate;
import com.se2035.jrw.entity.Job;
import com.se2035.jrw.enums.ApplicationStatus;
import com.se2035.jrw.exception.BadRequestException;
import com.se2035.jrw.exception.ResourceNotFoundException;
import com.se2035.jrw.enums.JobStatus;
import com.se2035.jrw.repository.ApplicationRepo;
import com.se2035.jrw.repository.CVRepo;
import com.se2035.jrw.repository.CandidateRepo;
import com.se2035.jrw.repository.JobRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepo applicationRepo;
    private final CandidateRepo candidateRepo;
    private final JobRepo jobRepo;
    private final CVRepo cvRepo;

    @Override
    public List<Application> findByJobId(Integer jobId) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        return applicationRepo.findByJobId(jobId);
    }

    @Override
    @Transactional
    public Application shortlist(Integer applicationId) {
        Application application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if(application.getStatus() != ApplicationStatus.PENDING) {
            throw new BadRequestException("Only pending applications can be shortlisted");
        }

        application.setStatus(ApplicationStatus.SHORTLISTED);

        return applicationRepo.save(application);
    }

    @Override
    @Transactional
    public Application apply(Integer candidateId, Integer jobId, Integer cvId) {
        Candidate candidate = candidateRepo.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        if (job.getStatus() != JobStatus.APPROVED) {
            throw new IllegalStateException("This job is no longer accepting applications");
        }

        if (job.getDeadline() != null && job.getDeadline().isBefore(java.time.LocalDate.now())) {
            throw new IllegalStateException("The deadline for this job has passed");
        }

        if (applicationRepo.existsByCandidateAndJob(candidate, job)) {
            throw new IllegalStateException("You have already applied for this job");
        }

        CV cv = cvRepo.findById(cvId)
                .orElseThrow(() -> new IllegalArgumentException("CV not found"));

        if (!cv.getCandidate().getCandidateId().equals(candidateId)) {
            throw new IllegalStateException("Invalid CV");
        }

        Application application = Application.builder()
                .candidate(candidate)
                .job(job)
                .cv(cv)
                .status(ApplicationStatus.PENDING)
                .build();

        return applicationRepo.save(application);
    }

    @Override
    public Page<Application> getMyApplications(Integer candidateId, Pageable pageable) {
        Candidate candidate = candidateRepo.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));
        return applicationRepo.findByCandidateOrderByAppliedAtDesc(candidate, pageable);
    }

    @Override
    @Transactional
    public Application reject(Integer applicationId) {
        Application application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if(application.getStatus() != ApplicationStatus.PENDING) {
            throw new BadRequestException("Only pending applications can be rejected");
        }

        application.setStatus(ApplicationStatus.REJECTED);

        return applicationRepo.save(application);
    }

    @Override
    @Transactional
    public void withdraw(Integer applicationId, Integer candidateId) {
        Application application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        if (!application.getCandidate().getCandidateId().equals(candidateId)) {
            throw new IllegalStateException("You do not have permission to perform this action");
        }

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("You can only withdraw applications that are PENDING");
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);
        applicationRepo.save(application);
    }

    @Override
    @Transactional
    public Application hire(Integer applicationId) {
        Application application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (application.getStatus() != ApplicationStatus.SHORTLISTED) {
            throw new BadRequestException("Only shortlist applications can be hire");
        }

        application.setStatus(ApplicationStatus.HIRED);

        return applicationRepo.save(application);

    }

    @Override
    public boolean hasApplied(Integer candidateId, Integer jobId) {
        return candidateRepo.findById(candidateId)
                .flatMap(c -> jobRepo.findById(jobId)
                        .map(j -> applicationRepo.existsByCandidateAndJob(c, j)))
                .orElse(false);
    }

    @Override
    public Application findById(Integer id) {
        return applicationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
    }

    @Override
    public List<Application> findAll() {
        return applicationRepo.findAll();
    }

    @Override
    public List<Application> findByRecruiterId(Integer recruiterId) {
        return applicationRepo.findByJob_Recruiter_RecruiterIdOrderByAppliedAtDesc(recruiterId);
    }

    @Override
    @Transactional
    public Application save(Application application) {
        return applicationRepo.save(application);
    }
}
