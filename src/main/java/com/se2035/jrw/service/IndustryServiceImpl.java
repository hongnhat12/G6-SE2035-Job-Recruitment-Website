package com.se2035.jrw.service;

import com.se2035.jrw.dto.IndustryRequest;
import com.se2035.jrw.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import com.se2035.jrw.entity.Industry;
import com.se2035.jrw.exception.ResourceNotFoundException;
import com.se2035.jrw.repository.IndustryRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    public void saveIndustry(IndustryRequest industryRequest) {
        String trimmedName = industryRequest.getIndustryName().trim();
        industryRequest.setIndustryName(trimmedName);

        Optional<Industry> foundIndustry = industryRepo.findByIndustryNameIgnoreCase(industryRequest.getIndustryName());
        if (foundIndustry.isPresent()) {
            if (industryRequest.getIndustryId() == null || !foundIndustry.get().getIndustryId().equals(industryRequest.getIndustryId())) {
                throw new BadRequestException("Industry name '" + industryRequest.getIndustryName() + "' already exists!");
            }
        }
        Industry industry;
        if (industryRequest.getIndustryId() == null) {
            industry = new Industry();
        } else {
            industry = industryRepo.findById(industryRequest.getIndustryId()).orElseThrow(() ->
                    new ResourceNotFoundException("Industry not found")
            );
        }
        industry.setIndustryName(industryRequest.getIndustryName());
        industry.setDescription(industryRequest.getDescription());

        industryRepo.save(industry);
    }

    @Override
    public void deleteIndustryById(Integer id) {
        Industry industry = industryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot delete. Industry not found with ID: " + id));
        if (industry.getJobs() != null && !industry.getJobs().isEmpty()) {
            throw new BadRequestException("Cannot delete industry '" + industry.getIndustryName() + "' because it has associated job postings.");
        }
        industryRepo.delete(industry);
    }
}