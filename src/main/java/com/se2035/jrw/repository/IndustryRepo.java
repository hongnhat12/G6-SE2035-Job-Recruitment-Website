package com.se2035.jrw.repository;

import com.se2035.jrw.entity.Industry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IndustryRepo extends JpaRepository<Industry, Integer> {
    Optional<Industry> findByIndustryName(String industryName);

    Optional<Industry> findByIndustryNameIgnoreCase(String industryName);

    Page<Industry> findByIndustryNameContainingIgnoreCase(String industryName, Pageable pageable);
}
