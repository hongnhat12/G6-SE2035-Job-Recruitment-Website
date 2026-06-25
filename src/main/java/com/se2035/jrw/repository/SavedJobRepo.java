package com.se2035.jrw.repository;

import com.se2035.jrw.entity.SavedJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedJobRepo extends JpaRepository<SavedJob, Integer> {
}
