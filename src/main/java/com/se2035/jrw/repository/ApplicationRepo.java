package com.se2035.jrw.repository;

import com.se2035.jrw.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepo extends JpaRepository<Application, Integer> {
}
