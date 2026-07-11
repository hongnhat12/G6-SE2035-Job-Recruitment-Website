package com.se2035.jrw.controller;

import com.se2035.jrw.dto.IndustryRequest;
import com.se2035.jrw.entity.Industry;
import com.se2035.jrw.service.IndustryService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/industries")
@RequiredArgsConstructor
public class IndustryController {

    private final IndustryService industryService;

    @GetMapping
    public String listIndustries(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {

        Page<Industry> industryPage;

        if (search != null && !search.trim().isEmpty()) {
            industryPage = industryService.searchIndustriesByName(search.trim(), PageRequest.of(page, size));
            model.addAttribute("search", search);
        } else {
            industryPage = industryService.getIndustriesWithPagination(PageRequest.of(page, size));
        }

        model.addAttribute("industries", industryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", industryPage.getTotalPages());
        model.addAttribute("totalItems", industryPage.getTotalElements());

        return "admin/industry/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("industry", new IndustryRequest());
        return "admin/industry/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Industry industry = industryService.getIndustryById(id);
        model.addAttribute("industry", industry);
        return "admin/industry/form";
    }

    @PostMapping("/save")
    public String saveIndustry(
            @ModelAttribute("industry") IndustryRequest industryRequest,
            RedirectAttributes redirectAttributes,
            Model model) {

        boolean hasError = false;

        if (industryRequest.getIndustryName() == null || industryRequest.getIndustryName().trim().isEmpty()) {
            model.addAttribute("nameError", "Industry name cannot be blank");
            hasError = true;
        } else if (industryRequest.getIndustryName().length() > 100) {
            model.addAttribute("nameError", "Industry name cannot exceed 100 characters");
            hasError = true;
        }

        if (hasError) {
            return "admin/industry/form";
        }

        try {
            industryService.saveIndustry(industryRequest);
            redirectAttributes.addFlashAttribute("success", "Industry saved successfully!");
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/industry/form";
        }

        return "redirect:/admin/industries";
    }

    @GetMapping("/delete/{id}")
    public String deleteIndustry(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            industryService.deleteIndustryById(id);
            redirectAttributes.addFlashAttribute("success", "Industry deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete: " + e.getMessage());
        }
        return "redirect:/admin/industries";
    }
}