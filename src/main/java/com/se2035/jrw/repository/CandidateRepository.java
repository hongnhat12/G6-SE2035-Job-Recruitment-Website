package com.se2035.jrw.repository;

import com.se2035.jrw.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate, Integer> {
    Optional<Candidate> findByUser_UserId(Integer userId);
}
