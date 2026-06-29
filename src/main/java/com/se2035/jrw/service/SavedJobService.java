package com.se2035.jrw.service;

import com.se2035.jrw.entity.Candidate;
import com.se2035.jrw.entity.Job;
import com.se2035.jrw.entity.SavedJob;
import com.se2035.jrw.entity.SavedJobId;
import com.se2035.jrw.enums.JobStatus;
import com.se2035.jrw.repository.CandidateRepository;
import com.se2035.jrw.repository.JobRepository;
import com.se2035.jrw.repository.SavedJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;

    @Transactional
    public void saveJob(Integer candidateId, Integer jobId) {
        if (savedJobRepository.existsByIdCandidateIdAndIdJobId(candidateId, jobId)) {
            return;
        }

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        if (job.getStatus() != JobStatus.APPROVED) {
            throw new IllegalStateException("Only active job postings can be saved");
        }

        SavedJob savedJob = SavedJob.builder()
                .id(new SavedJobId(candidateId, jobId))
                .candidate(candidate)
                .job(job)
                .build();

        savedJobRepository.save(savedJob);
    }

    @Transactional
    public void unsaveJob(Integer candidateId, Integer jobId) {
        savedJobRepository.deleteByIdCandidateIdAndIdJobId(candidateId, jobId);
    }

    public Page<SavedJob> getSavedJobs(Integer candidateId, Pageable pageable) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));
        return savedJobRepository.findByCandidateOrderBySavedAtDesc(candidate, pageable);
    }

    public boolean isSaved(Integer candidateId, Integer jobId) {
        return savedJobRepository.existsByIdCandidateIdAndIdJobId(candidateId, jobId);
    }
}
