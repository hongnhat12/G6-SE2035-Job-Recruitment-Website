package com.se2035.jrw.controller;

import com.se2035.jrw.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping({"", "/dashboard"})
    public String dashboard(Authentication auth, Model model) {
        if (auth == null || !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/login";
        }

        Map<String, Object> stats = adminDashboardService.getDashboardStats();
        model.addAllAttributes(stats);

        return "admin/dashboard";
    }
}