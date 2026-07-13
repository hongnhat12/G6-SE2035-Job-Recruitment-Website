package com.se2035.jrw.dto;

import com.se2035.jrw.entity.Application;
import com.se2035.jrw.entity.Job;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterDashboardDTO {

    private long totalJobs;
    private long activeJobs;
    private long pendingJobs;

    private long totalApplications;
    private long pendingApplications;
    private long shortlistedApplications;
    private long hiredApplications;
    private long rejectedApplications;

    // Keyed by enum name(), e.g. "APPROVED" -> 4
    private Map<String, Long> jobsByStatus;
    private Map<String, Long> applicationsByStatus;

    // Keyed by Job.jobId -> number of applications for that job.
    // Only contains entries for jobs that have at least one application.
    private Map<Integer, Long> applicantCountByJob;

    private List<Job> recentJobs;
    private List<Application> recentApplications;
}
