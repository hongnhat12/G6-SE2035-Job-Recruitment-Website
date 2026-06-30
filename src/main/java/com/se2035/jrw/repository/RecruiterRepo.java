package com.se2035.jrw.repository;

import com.se2035.jrw.entity.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RecruiterRepo extends JpaRepository<Recruiter, Integer> {
    Optional<Recruiter> findByUser_UserId(Integer userId);
}
