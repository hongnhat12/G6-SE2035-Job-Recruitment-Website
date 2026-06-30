package com.se2035.jrw.repository;

import com.se2035.jrw.entity.CV;
import com.se2035.jrw.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CVRepo extends JpaRepository<CV, Integer> {
    List<CV> findByCandidate(Candidate candidate);
    Optional<CV> findByCandidateAndIsDefaultTrue(Candidate candidate);
}
