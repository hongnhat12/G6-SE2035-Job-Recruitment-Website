package com.se2035.jrw.service;

import com.se2035.jrw.dto.JobRequest;
import com.se2035.jrw.entity.Job;
import com.se2035.jrw.entity.Recruiter;
import com.se2035.jrw.entity.User;
import com.se2035.jrw.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface JobService {
    Job createJob(JobRequest req);

    Job editJob(Integer jobId, JobRequest req);

    void deleteJob(Integer jobId);

    List<Job> findAll();

    Job closeJob(Integer jobId);

    List<Job> findByRecruiterId(Integer recruiterId);

    Page<Job> findPendingJobs(Pageable pageable);

    Job approveJob(Integer jobId, User adminUser);

    Job rejectJob(Integer jobId, User adminUser);

    Job findById(Integer id);

    List<Job> getMyJobs(Recruiter recruiter);

    List<Job> findLatestJobs();

    List<Job> findHottestJobs();

    List<String> findDistinctLocations();

    List<String> findDistinctEmploymentTypes();

    List<String> findDistinctIndustries();

    Page<Job> searchApprovedJobs(String keyword, String location, String employmentType, String industry, Pageable pageable);

    Optional<Job> findJobDetailWithAssociations(Integer id);

    Page<Job> findJobsByStatus(JobStatus status, Pageable pageable);
}
