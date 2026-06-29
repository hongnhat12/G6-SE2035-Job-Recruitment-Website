package com.se2035.jrw.service;

import com.se2035.jrw.entity.Job;
import com.se2035.jrw.enums.JobStatus;
import com.se2035.jrw.repository.ApplicationRepo;
import com.se2035.jrw.repository.CompanyRepo;
import com.se2035.jrw.repository.JobRepo;
import com.se2035.jrw.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepo userRepo;
    private final JobRepo jobRepo;
    private final ApplicationRepo applicationRepo;
    private final CompanyRepo companyRepo;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", userRepo.count());
        stats.put("totalJobs", jobRepo.countByStatus(JobStatus.APPROVED));
        stats.put("pendingJobsCount", jobRepo.countByStatus(JobStatus.PENDING));
        stats.put("totalApplications", applicationRepo.count());
        stats.put("totalCompanies", companyRepo.count());

        stats.put("avgSalary", jobRepo.findAverageSalaryMaxByStatus(JobStatus.APPROVED));
        stats.put("maxSalary", jobRepo.findHighestSalaryMaxByStatus(JobStatus.APPROVED));
        stats.put("minSalary", jobRepo.findLowestSalaryMinByStatus(JobStatus.APPROVED));

        stats.put("pendingJobs", jobRepo.findTop5ByStatusOrderByCreatedAtDesc(JobStatus.PENDING));

        List<Object[]> categoryList = jobRepo.countJobsByIndustryAndStatus(JobStatus.APPROVED);
        Map<String, Long> jobsByCategory = new HashMap<>();
        for (Object[] row : categoryList) {
            jobsByCategory.put((String) row[0], (Long) row[1]);
        }
        stats.put("jobsByCategory", jobsByCategory);

        List<Object[]> typeList = jobRepo.countJobsByEmploymentTypeAndStatus(JobStatus.APPROVED);
        Map<String, Long> jobsByType = new HashMap<>();
        for (Object[] row : typeList) {
            jobsByType.put((String) row[0], (Long) row[1]);
        }
        stats.put("jobsByType", jobsByType);

        List<Object[]> statusList = applicationRepo.countApplicationsByStatus();
        Map<String, Long> applicationsByStatus = new HashMap<>();
        for (Object[] row : statusList) {
            applicationsByStatus.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("applicationsByStatus", applicationsByStatus);

        List<Object[]> roleList = userRepo.countUsersByRole();
        Map<String, Long> usersByRole = new HashMap<>();
        for (Object[] row : roleList) {
            usersByRole.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("usersByRole", usersByRole);

        return stats;
    }
}
