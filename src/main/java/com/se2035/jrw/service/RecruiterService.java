package com.se2035.jrw.service;

import com.se2035.jrw.dto.RecruiterDashboardDTO;
import com.se2035.jrw.entity.Recruiter;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface RecruiterService {
    Recruiter getCurrentRecruiter(Authentication auth);
    RecruiterDashboardDTO getDashboardStats(Recruiter recruiter);
}
