package com.se2035.jrw.repository;

import com.se2035.jrw.entity.Candidate;
import com.se2035.jrw.entity.SavedJob;
import com.se2035.jrw.entity.SavedJobId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedJobRepo extends JpaRepository<SavedJob, SavedJobId> {

    @EntityGraph(attributePaths = {"job", "job.company"})
    Page<SavedJob> findByCandidateOrderBySavedAtDesc(Candidate candidate, Pageable pageable);

    boolean existsByIdCandidateIdAndIdJobId(Integer candidateId, Integer jobId);

    void deleteByIdCandidateIdAndIdJobId(Integer candidateId, Integer jobId);
}
