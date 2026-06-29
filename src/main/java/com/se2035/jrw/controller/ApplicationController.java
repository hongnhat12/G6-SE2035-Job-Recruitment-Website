package com.se2035.jrw.controller;

import com.se2035.jrw.config.SecurityUtils;
import com.se2035.jrw.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/my/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public String myApplications(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 Authentication auth,
                                 Model model) {
        Integer candidateId = securityUtils.getCandidateId(auth);
        var appPage = applicationService.getMyApplications(candidateId, PageRequest.of(page, size));

        model.addAttribute("applications", appPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", appPage.getTotalPages());

        return "applications";
    }

    @PostMapping("/{id}/withdraw")
    public String withdraw(@PathVariable Integer id,
                           Authentication auth,
                           RedirectAttributes redirectAttributes) {
        try {
            Integer candidateId = securityUtils.getCandidateId(auth);
            applicationService.withdraw(id, candidateId);
            redirectAttributes.addFlashAttribute("success", "Đã rút hồ sơ thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/my/applications";
    }
}
