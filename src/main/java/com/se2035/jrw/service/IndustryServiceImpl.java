package com.se2035.jrw.service;

import lombok.RequiredArgsConstructor;
import com.se2035.jrw.entity.Industry;
import com.se2035.jrw.exception.BadRequestException;
import com.se2035.jrw.exception.ResourceNotFoundException;
import com.se2035.jrw.repository.IndustryRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IndustryServiceImpl implements IndustryService {

    private final IndustryRepo industryRepo;

    @Override
    public List<Industry> getAllIndustries() {
        return industryRepo.findAll();
    }

    @Override
    public Page<Industry> getIndustriesWithPagination(Pageable pageable) {
        return industryRepo.findAll(pageable);
    }

    @Override
    public Page<Industry> searchIndustriesByName(String name, Pageable pageable) {
        return industryRepo.findByIndustryNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public Industry getIndustryById(Integer id) {
        return industryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Industry not found with ID: " + id));
    }

    @Override
    public void saveIndustry(Industry industry) {
        industryRepo.findByIndustryName(industry.getIndustryName())
                .ifPresent(existing -> {
                    if (industry.getIndustryId() == null || !existing.getIndustryId().equals(industry.getIndustryId())) {
                        throw new BadRequestException("Industry name '" + industry.getIndustryName() + "' already exists!");
                    }
                });

        industryRepo.save(industry);
    }

    @Override
    public void deleteIndustryById(Integer id) {
        if (!industryRepo.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Industry not found with ID: " + id);
        }
        industryRepo.deleteById(id);
    }
}