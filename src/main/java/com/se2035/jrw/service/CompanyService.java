package com.se2035.jrw.service;

import com.se2035.jrw.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CompanyService {
    List<Company> getAllCompanies();

    Page<Company> getCompaniesWithPagination(Pageable pageable);

    Page<Company> searchCompaniesByName(String name, Pageable pageable);

    Company getCompanyById(Integer id);

    void saveCompany(Company company);

    void deleteCompanyById(Integer id);
}