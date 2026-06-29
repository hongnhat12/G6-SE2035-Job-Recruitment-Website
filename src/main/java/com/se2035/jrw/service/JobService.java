package com.se2035.jrw.service;

import com.se2035.jrw.entity.Job;
import com.se2035.jrw.enums.JobStatus;
import com.se2035.jrw.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public List<Job> getFeaturedJobs() {
        return jobRepository.findTop8ByStatusOrderByCreatedAtDesc(JobStatus.APPROVED);
    }

    public Page<Job> searchJobs(String keyword, String location, String employmentType, String industry, Pageable pageable) {
        String kw = isBlank(keyword) ? null : keyword.trim();
        String loc = isBlank(location) ? null : location.trim();
        String type = isBlank(employmentType) ? null : employmentType.trim();
        String ind = isBlank(industry) ? null : industry.trim();
        return jobRepository.searchApprovedJobs(kw, loc, type, ind, pageable);
    }

    public Job getApprovedJobById(Integer jobId) {
        return jobRepository.findByJobIdAndStatus(jobId, JobStatus.APPROVED)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tin tuyển dụng: " + jobId));
    }

    public List<String> getDistinctLocations() {
        return jobRepository.findDistinctLocations();
    }

    public List<String> getDistinctEmploymentTypes() {
        return jobRepository.findDistinctEmploymentTypes();
    }

    public List<String> getDistinctIndustries() {
        return jobRepository.findDistinctIndustries();
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
