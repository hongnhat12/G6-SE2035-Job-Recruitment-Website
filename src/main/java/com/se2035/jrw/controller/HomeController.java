package com.se2035.jrw.controller;

import com.se2035.jrw.enums.JobStatus;
import com.se2035.jrw.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final JobRepository jobRepository;

    @GetMapping("/")
    public String home(Model model) {
        // Load latest jobs (8)
        model.addAttribute("latestJobs", jobRepository.findTop8ByStatusOrderByCreatedAtDesc(JobStatus.APPROVED));
        // Load hottest jobs (8)
        model.addAttribute("hottestJobs", jobRepository.findTop8ByStatusOrderBySalaryMaxDesc(JobStatus.APPROVED));
        // Load distinct locations
        model.addAttribute("locations", jobRepository.findDistinctLocations());
        return "home";
    }
}
