package com.se2035.jrw.repository;

import com.se2035.jrw.entity.Job;
import com.se2035.jrw.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobRepo extends JpaRepository<Job, Integer> {
    Page<Job> findByStatus(JobStatus status, Pageable pageable);

    List<Job> findTop8ByStatusOrderByCreatedAtDesc(JobStatus status);

    @EntityGraph(attributePaths = {"company"})
    List<Job> findTop8ByStatusOrderBySalaryMaxDesc(JobStatus status);

    @EntityGraph(attributePaths = {"company"})
    @Query("""
            SELECT j FROM Job j
            WHERE j.status = :status
              AND (:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(j.requiredSkills) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
              AND (:employmentType IS NULL OR j.employmentType = :employmentType)
              AND (:industry IS NULL OR j.industry.industryName = :industry)
            ORDER BY j.createdAt DESC
            """)
    Page<Job> searchApprovedJobs(
            @Param("status") JobStatus status,
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("employmentType") String employmentType,
            @Param("industry") String industry,
            Pageable pageable);

    @Query("SELECT DISTINCT j.location FROM Job j WHERE j.status = :status AND j.location IS NOT NULL ORDER BY j.location")
    List<String> findDistinctLocations(@Param("status") JobStatus status);

    @Query("SELECT DISTINCT j.employmentType FROM Job j WHERE j.status = :status AND j.employmentType IS NOT NULL ORDER BY j.employmentType")
    List<String> findDistinctEmploymentTypes(@Param("status") JobStatus status);

    @Query("SELECT DISTINCT j.industry.industryName FROM Job j WHERE j.status = :status AND j.industry IS NOT NULL ORDER BY j.industry.industryName")
    List<String> findDistinctIndustries(@Param("status") JobStatus status);


    @Query("""
            SELECT j FROM Job j
            JOIN FETCH j.company
            JOIN FETCH j.recruiter
            JOIN FETCH j.industry
            WHERE j.jobId = :jobId
            """)
    Optional<Job> findJobDetailWithAssociations(@Param("jobId") Integer jobId);

    long countByStatus(JobStatus status);

    @Query("SELECT COALESCE(AVG(j.salaryMax), 0.0) FROM Job j WHERE j.status = :status")
    Double findAverageSalaryMaxByStatus(@Param("status") JobStatus status);

    @Query("SELECT COALESCE(MAX(j.salaryMax), 0.0) FROM Job j WHERE j.status = :status")
    Double findHighestSalaryMaxByStatus(@Param("status") JobStatus status);

    @Query("SELECT COALESCE(MIN(j.salaryMin), 0.0) FROM Job j WHERE j.status = :status")
    Double findLowestSalaryMinByStatus(@Param("status") JobStatus status);

    List<Job> findTop5ByStatusOrderByCreatedAtDesc(JobStatus status);

    @Query("SELECT j.industry.industryName, COUNT(j) FROM Job j WHERE j.industry IS NOT NULL AND j.status = :status GROUP BY j.industry.industryName")
    List<Object[]> countJobsByIndustryAndStatus(@Param("status") JobStatus status);

    @Query("SELECT j.employmentType, COUNT(j) FROM Job j WHERE j.employmentType IS NOT NULL AND j.status = :status GROUP BY j.employmentType")
    List<Object[]> countJobsByEmploymentTypeAndStatus(@Param("status") JobStatus status);

    List<Job> findByRecruiterRecruiterId(Integer recruiterId);

    List<Job> findByRecruiterRecruiterIdAndStatusNot(
            Integer recruiterId,
            JobStatus status);
}
