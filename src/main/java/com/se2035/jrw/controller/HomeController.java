package com.se2035.jrw.controller;

import com.se2035.jrw.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final JobService jobService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("latestJobs", jobService.findLatestJobs());
        model.addAttribute("hottestJobs", jobService.findHottestJobs());
        model.addAttribute("locations", jobService.findDistinctLocations());
        return "home";
    }
}
