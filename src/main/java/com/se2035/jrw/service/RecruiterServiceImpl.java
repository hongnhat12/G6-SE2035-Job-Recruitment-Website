package com.se2035.jrw.service;

import com.se2035.jrw.dto.RecruiterDashboardDTO;
import com.se2035.jrw.entity.Application;
import com.se2035.jrw.entity.Job;
import com.se2035.jrw.entity.Recruiter;
import com.se2035.jrw.enums.ApplicationStatus;
import com.se2035.jrw.enums.JobStatus;
import com.se2035.jrw.repository.JobRepo;
import com.se2035.jrw.repository.RecruiterRepo;
import com.se2035.jrw.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruiterServiceImpl implements RecruiterService {

    private final UserRepo userRepo;
    private final RecruiterRepo recruiterRepo;
    private final JobRepo jobRepo;
    private final JobService jobService;
    private final ApplicationService applicationService;

    @Override
    public Recruiter getCurrentRecruiter(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("You are not logged in");
        }
        String email = auth.getName();
        return userRepo.findByEmail(email)
                .flatMap(u -> recruiterRepo.findByUser_UserId(u.getUserId()))
                .orElseThrow(() -> new IllegalStateException("Recruiter profile not found"));
    }

//    @Override
//    public Map<String, Object> getDashboardStats(Recruiter recruiter) {
//        Long totalJobs = jobRepo.countByRecruiterRecruiterId(recruiter.getRecruiterId());
//        Long activeJobs = jobRepo.countByRecruiterRecruiterIdandStatus(recruiter.getRecruiterId(), JobStatus.APPROVED);
//        Long totalApplications =
//
//        Map<String, Object> stats = new LinkedHashMap<>();
//        stats.put("totalJobs", totalJobs);
//        stats.put("activeJobs", activeJobs);
//        stats.put("pendingJobs", jobsByStatus.getOrDefault(JobStatus.PENDING.name(), 0L));
//        stats.put("totalApplications", myApplications.size());
//        stats.put("pendingApplications", applicationsByStatus.getOrDefault(ApplicationStatus.PENDING.name(), 0L));
//        stats.put("shortlistedApplications", applicationsByStatus.getOrDefault(ApplicationStatus.SHORTLISTED.name(), 0L));
//        stats.put("hiredApplications", applicationsByStatus.getOrDefault(ApplicationStatus.HIRED.name(), 0L));
//        stats.put("rejectedApplications", applicationsByStatus.getOrDefault(ApplicationStatus.REJECTED.name(), 0L));
//        stats.put("jobsByStatus", jobsByStatus);
//        stats.put("applicationsByStatus", applicationsByStatus);
//        stats.put("applicantCountByJob", applicantCountByJob);
//        stats.put("recentJobs", recentJobs);
//        stats.put("recentApplications", recentApplications);
//        return stats;
//    }

    @Override
    public RecruiterDashboardDTO getDashboardStats(Recruiter recruiter) {
        List<Job> myJobs = jobService.getMyJobs(recruiter);
        List<Application> myApplications = applicationService.findByRecruiterId(recruiter.getRecruiterId());

        Map<String, Long> jobsByStatus = myJobs.stream()
                .collect(Collectors.groupingBy(j -> j.getStatus().name(), Collectors.counting()));

        Map<String, Long> applicationsByStatus = myApplications.stream()
                .collect(Collectors.groupingBy(a -> a.getStatus().name(), Collectors.counting()));

        // a.getJob().getJobId() only reads the FK column, so it does not trigger
        // a lazy-load of the full Job entity.
        Map<Integer, Long> applicantCountByJob = myApplications.stream()
                .collect(Collectors.groupingBy(a -> a.getJob().getJobId(), Collectors.counting()));

        List<Job> recentJobs = myJobs.stream()
                .sorted(Comparator.comparing(Job::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .collect(Collectors.toList());

        // myApplications is already ordered by appliedAt desc (findByRecruiterId ->
        // findByJob_Recruiter_RecruiterIdOrderByAppliedAtDesc), so just take the head.
        List<Application> recentApplications = myApplications.stream()
                .limit(5)
                .collect(Collectors.toList());

        return RecruiterDashboardDTO.builder()
                .totalJobs(myJobs.size())
                .activeJobs(jobsByStatus.getOrDefault(JobStatus.APPROVED.name(), 0L))
                .pendingJobs(jobsByStatus.getOrDefault(JobStatus.PENDING.name(), 0L))
                .totalApplications(myApplications.size())
                .pendingApplications(applicationsByStatus.getOrDefault(ApplicationStatus.PENDING.name(), 0L))
                .shortlistedApplications(applicationsByStatus.getOrDefault(ApplicationStatus.SHORTLISTED.name(), 0L))
                .hiredApplications(applicationsByStatus.getOrDefault(ApplicationStatus.HIRED.name(), 0L))
                .rejectedApplications(applicationsByStatus.getOrDefault(ApplicationStatus.REJECTED.name(), 0L))
                .jobsByStatus(jobsByStatus)
                .applicationsByStatus(applicationsByStatus)
                .applicantCountByJob(applicantCountByJob)
                .recentJobs(recentJobs)
                .recentApplications(recentApplications)
                .build();
    }
}
