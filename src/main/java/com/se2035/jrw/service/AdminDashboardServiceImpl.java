package com.se2035.jrw.service;

import com.se2035.jrw.entity.Job;
import com.se2035.jrw.enums.JobStatus;
import com.se2035.jrw.repository.ApplicationRepository;
import com.se2035.jrw.repository.CompanyRepository;
import com.se2035.jrw.repository.JobRepository;
import com.se2035.jrw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final CompanyRepository companyRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", userRepository.count());
        stats.put("totalJobs", jobRepository.countByStatus(JobStatus.APPROVED));
        stats.put("pendingJobsCount", jobRepository.countByStatus(JobStatus.PENDING));
        stats.put("totalApplications", applicationRepository.count());
        stats.put("totalCompanies", companyRepository.count());

        stats.put("avgSalary", jobRepository.findAverageSalaryMaxByStatus(JobStatus.APPROVED));
        stats.put("maxSalary", jobRepository.findHighestSalaryMaxByStatus(JobStatus.APPROVED));
        stats.put("minSalary", jobRepository.findLowestSalaryMinByStatus(JobStatus.APPROVED));

        stats.put("pendingJobs", jobRepository.findTop5ByStatusOrderByCreatedAtDesc(JobStatus.PENDING));

        List<Object[]> categoryList = jobRepository.countJobsByIndustryAndStatus(JobStatus.APPROVED);
        Map<String, Long> jobsByCategory = new HashMap<>();
        for (Object[] row : categoryList) {
            jobsByCategory.put((String) row[0], (Long) row[1]);
        }
        stats.put("jobsByCategory", jobsByCategory);

        List<Object[]> typeList = jobRepository.countJobsByEmploymentTypeAndStatus(JobStatus.APPROVED);
        Map<String, Long> jobsByType = new HashMap<>();
        for (Object[] row : typeList) {
            jobsByType.put((String) row[0], (Long) row[1]);
        }
        stats.put("jobsByType", jobsByType);

        List<Object[]> statusList = applicationRepository.countApplicationsByStatus();
        Map<String, Long> applicationsByStatus = new HashMap<>();
        for (Object[] row : statusList) {
            applicationsByStatus.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("applicationsByStatus", applicationsByStatus);

        List<Object[]> roleList = userRepository.countUsersByRole();
        Map<String, Long> usersByRole = new HashMap<>();
        for (Object[] row : roleList) {
            usersByRole.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("usersByRole", usersByRole);

        return stats;
    }
}
