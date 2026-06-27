package com.se2035.jrw.repository;

import com.se2035.jrw.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepo extends JpaRepository<Job, Integer> {
    List<Job> findByRecruiterId(Integer recruiterId);
}
