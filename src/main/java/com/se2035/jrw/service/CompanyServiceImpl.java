package com.se2035.jrw.service;

import com.se2035.jrw.dto.CompanyRequest;
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
import java.util.Optional;

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
    public void saveCompany(CompanyRequest companyRequest) {
        String trimmedName = companyRequest.getCompanyName().trim();
        companyRequest.setCompanyName(trimmedName);

        Optional<Company> existing = companyRepo.findByCompanyNameIgnoreCase(companyRequest.getCompanyName());
        if (existing.isPresent()) {
            if (companyRequest.getCompanyId() == null || !existing.get().getCompanyId().equals(companyRequest.getCompanyId())) {
                throw new BadRequestException("Company name '" + companyRequest.getCompanyName() + "' already exists!");
            }
        }

        Company company;
        if (companyRequest.getCompanyId() == null) {
            company = new Company();
        } else {
            company = companyRepo.findById(companyRequest.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyRequest.getCompanyId()));
        }
        company.setCompanyName(companyRequest.getCompanyName());
        company.setDescription(companyRequest.getDescription());
        company.setWebsite(companyRequest.getWebsite());
        company.setEmail(companyRequest.getEmail());
        company.setPhone(companyRequest.getPhone());
        company.setAddress(companyRequest.getAddress());
        company.setLogo(companyRequest.getLogo());
        company.setStatus(companyRequest.getStatus());

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