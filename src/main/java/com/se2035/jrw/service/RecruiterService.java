package com.se2035.jrw.service;

import com.se2035.jrw.entity.Recruiter;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface RecruiterService {
    Recruiter getCurrentRecruiter(Authentication auth);
    Map<String, Object> getDashboardStats(Recruiter recruiter);
}
