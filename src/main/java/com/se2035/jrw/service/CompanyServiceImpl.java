package com.se2035.jrw.service;

import com.se2035.jrw.entity.Company;
import com.se2035.jrw.exception.BadRequestException;
import com.se2035.jrw.exception.ResourceNotFoundException;
import com.se2035.jrw.repository.CompanyRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepo companyRepo;

    @Override
    public List<Company> getAllCompanies() {
        return companyRepo.findAll();
    }

    @Override
    public Page<Company> getCompaniesWithPagination(Pageable pageable) {
        return companyRepo.findAll(pageable);
    }

    @Override
    public Page<Company> searchCompaniesByName(String name, Pageable pageable) {
        return companyRepo.findByCompanyNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public Company getCompanyById(Integer id) {
        return companyRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + id));
    }

    @Override
    public void saveCompany(Company company) {
        if (company.getCompanyName() != null) {
            company.setCompanyName(company.getCompanyName().trim());
        }

        companyRepo.findByCompanyNameIgnoreCase(company.getCompanyName())
                .ifPresent(existing -> {
                    if (company.getCompanyId() == null || !existing.getCompanyId().equals(company.getCompanyId())) {
                        throw new BadRequestException("Company name '" + company.getCompanyName() + "' already exists!");
                    }
                });

        companyRepo.save(company);
    }

    @Override
    public void deleteCompanyById(Integer id) {
        if (!companyRepo.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Company not found with ID: " + id);
        }
        companyRepo.deleteById(id);
    }
}