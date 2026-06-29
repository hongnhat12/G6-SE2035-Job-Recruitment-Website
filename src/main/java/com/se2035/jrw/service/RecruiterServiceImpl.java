package com.se2035.jrw.service;

import com.se2035.jrw.entity.Recruiter;
import com.se2035.jrw.repository.RecruiterRepo;
import com.se2035.jrw.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecruiterServiceImpl implements RecruiterService {

    private final UserRepo userRepo;
    private final RecruiterRepo recruiterRepo;

    @Override
    public Recruiter getCurrentRecruiter(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("You are not logged in");
        }
        String email = auth.getName();
        return userRepo.findByEmail(email)
                .flatMap(u -> recruiterRepo.findByUser_UserId(u.getUserId()))
                .orElseThrow(() -> new IllegalStateException("Recruiter profile not found"));
    }
}
