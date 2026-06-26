package com.se2035.jrw.controller;

import com.se2035.jrw.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @PostMapping
    public String createJob() {
        return "job/list";
    }

    @GetMapping()
    public String showJobDetail() {
        return "job/JobDetail";
    }
}
