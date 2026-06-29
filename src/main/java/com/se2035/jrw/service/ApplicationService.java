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
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy candidate"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tin tuyển dụng"));

        if (job.getStatus() != JobStatus.APPROVED) {
            throw new IllegalStateException("Tin tuyển dụng này không còn nhận hồ sơ");
        }

        if (job.getDeadline() != null && job.getDeadline().isBefore(java.time.LocalDate.now())) {
            throw new IllegalStateException("Tin tuyển dụng đã hết hạn nộp hồ sơ");
        }

        if (applicationRepository.existsByCandidateAndJob(candidate, job)) {
            throw new IllegalStateException("Bạn đã nộp hồ sơ cho vị trí này rồi");
        }

        CV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy CV"));

        if (!cv.getCandidate().getCandidateId().equals(candidateId)) {
            throw new IllegalStateException("CV không hợp lệ");
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
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy candidate"));
        return applicationRepository.findByCandidateOrderByAppliedAtDesc(candidate, pageable);
    }

    @Transactional
    public void withdraw(Integer applicationId, Integer candidateId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn ứng tuyển"));

        if (!application.getCandidate().getCandidateId().equals(candidateId)) {
            throw new IllegalStateException("Bạn không có quyền thực hiện thao tác này");
        }

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("Chỉ có thể rút đơn khi trạng thái là Đang chờ duyệt");
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
