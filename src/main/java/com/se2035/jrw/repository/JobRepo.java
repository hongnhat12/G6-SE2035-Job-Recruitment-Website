package com.se2035.jrw.repository;

import com.se2035.jrw.entity.Job;
import com.se2035.jrw.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepo extends JpaRepository<Job, Integer> {
    Page<Job> findByStatus(JobStatus status, Pageable pageable);
}
