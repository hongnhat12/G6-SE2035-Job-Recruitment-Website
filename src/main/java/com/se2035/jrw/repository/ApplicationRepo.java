package com.se2035.jrw.repository;

import com.se2035.jrw.entity.Application;
import com.se2035.jrw.entity.Candidate;
import com.se2035.jrw.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepo extends JpaRepository<Application, Integer> {

    Page<Application> findByCandidateOrderByAppliedAtDesc(Candidate candidate, Pageable pageable);

    Optional<Application> findByCandidateAndJob(Candidate candidate, Job job);

    boolean existsByCandidateAndJob(Candidate candidate, Job job);

    long countByJob(Job job);

    List<Application> findByJob_Recruiter_RecruiterIdOrderByAppliedAtDesc(Integer recruiterId);

    List<Application> findByJob_JobIdOrderByAppliedAtDesc(Integer jobId);

    @Query("SELECT a.status, COUNT(a) FROM Application a GROUP BY a.status")
    List<Object[]> countApplicationsByStatus();

    List<Application> findByJobId(Integer jobId);
}
