package com.fsm.shopflw.controller;

import com.fsm.shopflw.dto.dashboard.AdminDashboardResponse;
import com.fsm.shopflw.dto.dashboard.SellerDashboardResponse;
import com.fsm.shopflw.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminDashboardResponse adminDashboard() {
        return dashboardService.adminDashboard();
    }

    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    public SellerDashboardResponse sellerDashboard() {
        return dashboardService.sellerDashboard();
    }
}
