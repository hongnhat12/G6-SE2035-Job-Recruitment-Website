package com.se2035.jrw.controller;

import com.se2035.jrw.enums.JobStatus;
import com.se2035.jrw.repository.JobRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final JobRepo jobRepo;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("latestJobs", jobRepo.findTop8ByStatusOrderByCreatedAtDesc(JobStatus.APPROVED));
        model.addAttribute("hottestJobs", jobRepo.findTop8ByStatusOrderBySalaryMaxDesc(JobStatus.APPROVED));
        model.addAttribute("locations", jobRepo.findDistinctLocations(JobStatus.APPROVED));
        return "home";
    }
}
