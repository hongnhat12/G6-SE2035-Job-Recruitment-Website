package com.se2035.jrw.repository;

import com.se2035.jrw.entity.Job;
import com.se2035.jrw.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Integer>, JpaSpecificationExecutor<Job> {

    @EntityGraph(attributePaths = {"company"})
    List<Job> findTop8ByStatusOrderByCreatedAtDesc(JobStatus status);

    @Query(value = """
            SELECT j FROM Job j
            JOIN FETCH j.company
            WHERE j.status = com.se2035.jrw.enums.JobStatus.APPROVED
              AND (:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(j.requiredSkills) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
              AND (:employmentType IS NULL OR j.employmentType = :employmentType)
              AND (:industry IS NULL OR j.industry = :industry)
            ORDER BY j.createdAt DESC
            """,
           countQuery = """
            SELECT COUNT(j) FROM Job j
            WHERE j.status = com.se2035.jrw.enums.JobStatus.APPROVED
              AND (:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(j.requiredSkills) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
              AND (:employmentType IS NULL OR j.employmentType = :employmentType)
              AND (:industry IS NULL OR j.industry = :industry)
            """)
    Page<Job> searchApprovedJobs(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("employmentType") String employmentType,
            @Param("industry") String industry,
            Pageable pageable);

    @Query("SELECT DISTINCT j.location FROM Job j WHERE j.status = com.se2035.jrw.enums.JobStatus.APPROVED AND j.location IS NOT NULL ORDER BY j.location")
    List<String> findDistinctLocations();

    @Query("SELECT DISTINCT j.employmentType FROM Job j WHERE j.status = com.se2035.jrw.enums.JobStatus.APPROVED AND j.employmentType IS NOT NULL ORDER BY j.employmentType")
    List<String> findDistinctEmploymentTypes();

    @Query("SELECT DISTINCT j.industry FROM Job j WHERE j.status = com.se2035.jrw.enums.JobStatus.APPROVED AND j.industry IS NOT NULL ORDER BY j.industry")
    List<String> findDistinctIndustries();

    @EntityGraph(attributePaths = {"company", "recruiter"})
    Optional<Job> findByJobIdAndStatus(Integer jobId, JobStatus status);
}
