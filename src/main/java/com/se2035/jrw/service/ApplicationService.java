package com.se2035.jrw.service;

import com.se2035.jrw.entity.Application;
import com.se2035.jrw.entity.SavedJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ApplicationService {

    Application apply(Integer candidateId, Integer jobId, Integer cvId);

    Page<Application> getMyApplications(Integer candidateId, Pageable pageable);

    void withdraw(Integer applicationId, Integer candidateId);

    boolean hasApplied(Integer candidateId, Integer jobId);

    List<Application> findByJobId(Integer jobId);
    Application shortlist(Integer applicationId, Integer recruiterId);
    Application reject(Integer applicationId, Integer recruiterId);
    Application hire(Integer applicationId, Integer recruiterId);
    List<Application> findByRecruiterId(Integer recruiterId);
}