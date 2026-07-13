package com.se2035.jrw.controller;

import com.se2035.jrw.dto.CompanyRequest;
import com.se2035.jrw.entity.Company;
import com.se2035.jrw.service.CompanyService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public String listCompanies(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {

        Page<Company> companyPage;

        if (search != null && !search.trim().isEmpty()) {
            companyPage = companyService.searchCompaniesByName(search.trim(), PageRequest.of(page, size));
            model.addAttribute("search", search);
        } else {
            companyPage = companyService.getCompaniesWithPagination(PageRequest.of(page, size));
        }

        model.addAttribute("companies", companyPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", companyPage.getTotalPages());
        model.addAttribute("totalItems", companyPage.getTotalElements());

        return "admin/company/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("company", new Company());
        return "admin/company/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Company company = companyService.getCompanyById(id);
        model.addAttribute("company", company);
        return "admin/company/form";
    }

    @PostMapping("/save")
    public String saveCompany(
            @Valid @ModelAttribute("company") CompanyRequest companyRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            if (bindingResult.hasFieldErrors("companyName")) {
                model.addAttribute("nameError", bindingResult.getFieldError("companyName").getDefaultMessage());
            }
            if (bindingResult.hasFieldErrors("email")) {
                model.addAttribute("emailError", bindingResult.getFieldError("email").getDefaultMessage());
            }
            if (bindingResult.hasFieldErrors("phone")) {
                model.addAttribute("phoneError", bindingResult.getFieldError("phone").getDefaultMessage());
            }
            if (bindingResult.hasFieldErrors("website")) {
                model.addAttribute("websiteError", bindingResult.getFieldError("website").getDefaultMessage());
            }
            if (bindingResult.hasFieldErrors("address")) {
                model.addAttribute("addressError", bindingResult.getFieldError("address").getDefaultMessage());
            }
            return "admin/company/form";
        }

        try {
            companyService.saveCompany(companyRequest);
            redirectAttributes.addFlashAttribute("success", "Company saved successfully!");
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/company/form";
        }

        return "redirect:/admin/companies";
    }

    @GetMapping("/delete/{id}")
    public String deleteCompany(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            companyService.deleteCompanyById(id);
            redirectAttributes.addFlashAttribute("success", "Company deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete: " + e.getMessage());
        }
        return "redirect:/admin/companies";
    }
}