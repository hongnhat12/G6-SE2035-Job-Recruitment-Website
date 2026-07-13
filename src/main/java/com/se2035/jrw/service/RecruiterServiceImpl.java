package com.se2035.jrw.service;

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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecruiterServiceImpl implements RecruiterService {

    private final UserRepo userRepo;
    private final RecruiterRepo recruiterRepo;
    private final JobRepo jobRepo;

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

    @Override
    public Map<String, Object> getDashboardStats(Recruiter recruiter) {
        Long totalJobs = jobRepo.countByRecruiterRecruiterId(recruiter.getRecruiterId());
        Long activeJobs = jobRepo.countByRecruiterRecruiterIdandStatus(recruiter.getRecruiterId(), JobStatus.APPROVED);
        Long totalApplications =

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalJobs", totalJobs);
        stats.put("activeJobs", activeJobs);
        stats.put("pendingJobs", jobsByStatus.getOrDefault(JobStatus.PENDING.name(), 0L));
        stats.put("totalApplications", myApplications.size());
        stats.put("pendingApplications", applicationsByStatus.getOrDefault(ApplicationStatus.PENDING.name(), 0L));
        stats.put("shortlistedApplications", applicationsByStatus.getOrDefault(ApplicationStatus.SHORTLISTED.name(), 0L));
        stats.put("hiredApplications", applicationsByStatus.getOrDefault(ApplicationStatus.HIRED.name(), 0L));
        stats.put("rejectedApplications", applicationsByStatus.getOrDefault(ApplicationStatus.REJECTED.name(), 0L));
        stats.put("jobsByStatus", jobsByStatus);
        stats.put("applicationsByStatus", applicationsByStatus);
        stats.put("applicantCountByJob", applicantCountByJob);
        stats.put("recentJobs", recentJobs);
        stats.put("recentApplications", recentApplications);
        return stats;
    }
}
