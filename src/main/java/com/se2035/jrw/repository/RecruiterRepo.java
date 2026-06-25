package com.se2035.jrw.repository;

import com.se2035.jrw.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruiterRepo extends JpaRepository<Candidate, Integer> {
}
