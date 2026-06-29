package com.se2035.jrw.controller;

import com.se2035.jrw.config.SecurityUtils;
import com.se2035.jrw.service.SavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/my/saved-jobs")
@RequiredArgsConstructor
public class SavedJobController {

    private final SavedJobService savedJobService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public String savedJobs(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Authentication auth,
                            Model model) {
        Integer candidateId = securityUtils.getCandidateId(auth);
        var savedPage = savedJobService.getSavedJobs(candidateId, PageRequest.of(page, size));

        model.addAttribute("savedJobs", savedPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", savedPage.getTotalPages());

        return "saved-jobs";
    }

    @PostMapping("/{jobId}/remove")
    public String remove(@PathVariable Integer jobId,
                         Authentication auth,
                         RedirectAttributes redirectAttributes) {
        try {
            Integer candidateId = securityUtils.getCandidateId(auth);
            savedJobService.unsaveJob(candidateId, jobId);
            redirectAttributes.addFlashAttribute("success", "Đã xóa khỏi danh sách lưu");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/my/saved-jobs";
    }
}
