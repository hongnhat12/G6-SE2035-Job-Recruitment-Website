package com.se2035.jrw.config;

import com.se2035.jrw.repository.CandidateRepo;
import com.se2035.jrw.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepo userRepo;
    private final CandidateRepo candidateRepo;

    public Integer getCandidateId(Authentication auth) {
        String email = auth.getName();
        return userRepo.findByEmail(email)
                .flatMap(u -> candidateRepo.findByUser_UserId(u.getUserId()))
                .map(c -> c.getCandidateId())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy hồ sơ ứng viên"));
    }
}
