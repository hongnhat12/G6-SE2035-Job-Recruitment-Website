package com.se2035.jrw.service;

import com.se2035.jrw.entity.Application;
import com.se2035.jrw.entity.Job;
import com.se2035.jrw.enums.ApplicationStatus;
import com.se2035.jrw.exception.BadRequestException;
import com.se2035.jrw.exception.ResourceNotFoundException;
import com.se2035.jrw.repository.ApplicationRepo;
import com.se2035.jrw.repository.JobRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepo applicationRepo;
    private final JobRepo jobRepo;

    @Override
    public List<Application> findByJobId(Integer jobId) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        return applicationRepo.findByJobId(jobId);
    }

    @Override
    @Transactional
    public Application shortlist(Integer applicationId) {
        Application application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if(application.getStatus() != ApplicationStatus.PENDING) {
            throw new BadRequestException("Only pending applications can be shortlisted");
        }

        application.setStatus(ApplicationStatus.SHORTLISTED);

        return applicationRepo.save(application);
    }

    @Override
    @Transactional
    public Application reject(Integer applicationId) {
        Application application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if(application.getStatus() != ApplicationStatus.PENDING) {
            throw new BadRequestException("Only pending applications can be rejected");
        }

        application.setStatus(ApplicationStatus.REJECTED);

        return applicationRepo.save(application);
    }

    @Override
    @Transactional
    public Application hire(Integer applicationId) {
        Application application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if(application.getStatus() != ApplicationStatus.SHORTLISTED) {
            throw new BadRequestException("Only shortlist applications can be hire");
        }

        application.setStatus(ApplicationStatus.HIRED);

        return applicationRepo.save(application);
    }
}
