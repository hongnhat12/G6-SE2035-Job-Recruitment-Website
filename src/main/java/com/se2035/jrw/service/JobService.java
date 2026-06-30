package com.se2035.jrw.service;

import com.se2035.jrw.dto.JobRequest;
import com.se2035.jrw.entity.Job;
import com.se2035.jrw.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobService {
    Job createJob(JobRequest req);

    Job editJob(Integer jobId, JobRequest req);

    void deleteJob(Integer jobId);

    List<Job> findAll();

    Job closeJob(Integer jobId);

    Page<Job> findPendingJobs(Pageable pageable);

    Job approveJob(Integer jobId, User adminUser);

    Job rejectJob(Integer jobId, User adminUser);
}
