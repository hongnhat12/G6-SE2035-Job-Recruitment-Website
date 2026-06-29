package com.se2035.jrw.service;

import com.se2035.jrw.entity.SavedJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SavedJobService {

    void saveJob(Integer candidateId, Integer jobId);

    void unsaveJob(Integer candidateId, Integer jobId);

    Page<SavedJob> getSavedJobs(Integer candidateId, Pageable pageable);

    boolean isSaved(Integer candidateId, Integer jobId);
}

