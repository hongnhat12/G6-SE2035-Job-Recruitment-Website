package com.se2035.jrw.repository;

import com.se2035.jrw.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepo extends JpaRepository<Application, Integer> {
    List<Application> findByJobId(Integer jobId);
}
