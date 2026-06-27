package com.se2035.jrw.service;

import com.se2035.jrw.entity.Application;

import java.util.List;

public interface ApplicationService {
    List<Application> findByJobId(Integer jobId);
    Application shortlist(Integer applicationId);
    Application reject(Integer applicationId);
    Application hire(Integer applicationId);
}
