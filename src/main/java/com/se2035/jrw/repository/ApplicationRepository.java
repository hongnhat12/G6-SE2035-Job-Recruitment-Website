package com.se2035.jrw.repository;

import com.se2035.jrw.entity.Application;
import com.se2035.jrw.entity.Candidate;
import com.se2035.jrw.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    @EntityGraph(attributePaths = {"job", "job.company", "cv"})
    Page<Application> findByCandidateOrderByAppliedAtDesc(Candidate candidate, Pageable pageable);

    Optional<Application> findByCandidateAndJob(Candidate candidate, Job job);

    boolean existsByCandidateAndJob(Candidate candidate, Job job);

    long countByJob(Job job);

    @EntityGraph(attributePaths = {"job", "job.company", "candidate", "cv"})
    java.util.List<Application> findByJob_Recruiter_RecruiterIdOrderByAppliedAtDesc(Integer recruiterId);

    @EntityGraph(attributePaths = {"job", "job.company", "candidate", "cv"})
    java.util.List<Application> findByJob_JobIdOrderByAppliedAtDesc(Integer jobId);
}
