package com.se2035.jrw.repository;

import com.se2035.jrw.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepo extends JpaRepository<Company, Integer> {
    Page<Company> findByCompanyNameContainingIgnoreCase(String companyName, Pageable pageable);

    Optional<Company> findByCompanyNameIgnoreCase(String companyName);
}