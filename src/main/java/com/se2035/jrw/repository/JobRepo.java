package com.se2035.jrw.repository;

import com.se2035.jrw.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepo extends JpaRepository<Job, Integer> {
}
