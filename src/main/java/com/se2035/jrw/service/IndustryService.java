package com.se2035.jrw.service;

import com.se2035.jrw.dto.IndustryRequest;
import com.se2035.jrw.entity.Industry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IndustryService {
    List<Industry> getAllIndustries();

    Page<Industry> getIndustriesWithPagination(Pageable pageable);

    Page<Industry> searchIndustriesByName(String name, Pageable pageable);

    Industry getIndustryById(Integer id);

    void saveIndustry(IndustryRequest industryRequest);

    void deleteIndustryById(Integer id);
}