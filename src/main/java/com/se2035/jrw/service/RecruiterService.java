package com.se2035.jrw.service;

import com.se2035.jrw.entity.Recruiter;
import org.springframework.security.core.Authentication;

public interface RecruiterService {
    Recruiter getCurrentRecruiter(Authentication auth);
}
