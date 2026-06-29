package com.se2035.jrw.config;

import com.se2035.jrw.repository.CandidateRepository;
import com.se2035.jrw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;

    public Integer getCandidateId(Authentication auth) {
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .flatMap(u -> candidateRepository.findByUser_UserId(u.getUserId()))
                .map(c -> c.getCandidateId())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy hồ sơ ứng viên"));
    }
}
