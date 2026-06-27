package com.se2035.jrw.service;

import com.se2035.jrw.dto.JobRequest;
import com.se2035.jrw.entity.Job;

import java.util.List;

public interface JobService {
    Job createJob(JobRequest req);
    Job editJob(Integer jobId, JobRequest req);
    void deleteJob(Integer jobId);
    List<Job> findAll();
    Job closeJob(Integer jobId);
    List<Job> findByRecruiterId(Integer recruiterId);
}
