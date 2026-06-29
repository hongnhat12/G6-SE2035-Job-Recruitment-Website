package com.se2035.jrw.service;

import com.se2035.jrw.entity.Application;
import com.se2035.jrw.entity.CV;
import com.se2035.jrw.entity.Candidate;
import com.se2035.jrw.entity.Job;
import com.se2035.jrw.enums.ApplicationStatus;
import com.se2035.jrw.enums.JobStatus;
import com.se2035.jrw.repository.ApplicationRepository;
import com.se2035.jrw.repository.CVRepository;
import com.se2035.jrw.repository.CandidateRepository;
import com.se2035.jrw.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;
    private final CVRepository cvRepository;

    @Transactional
    public Application apply(Integer candidateId, Integer jobId, Integer cvId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        if (job.getStatus() != JobStatus.APPROVED) {
            throw new IllegalStateException("This job is no longer accepting applications");
        }

        if (job.getDeadline() != null && job.getDeadline().isBefore(java.time.LocalDate.now())) {
            throw new IllegalStateException("The deadline for this job has passed");
        }

        if (applicationRepository.existsByCandidateAndJob(candidate, job)) {
            throw new IllegalStateException("You have already applied for this job");
        }

        CV cv = cvRepository.findById(cvId)
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

        return applicationRepository.save(application);
    }

    public Page<Application> getMyApplications(Integer candidateId, Pageable pageable) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));
        return applicationRepository.findByCandidateOrderByAppliedAtDesc(candidate, pageable);
    }

    @Transactional
    public void withdraw(Integer applicationId, Integer candidateId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        if (!application.getCandidate().getCandidateId().equals(candidateId)) {
            throw new IllegalStateException("You do not have permission to perform this action");
        }

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("You can only withdraw applications that are PENDING");
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);
        applicationRepository.save(application);
    }

    public boolean hasApplied(Integer candidateId, Integer jobId) {
        return candidateRepository.findById(candidateId)
                .flatMap(c -> jobRepository.findById(jobId)
                        .map(j -> applicationRepository.existsByCandidateAndJob(c, j)))
                .orElse(false);
    }
}
