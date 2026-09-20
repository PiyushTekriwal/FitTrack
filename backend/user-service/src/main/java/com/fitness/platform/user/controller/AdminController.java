package com.fitness.platform.user.controller;

import com.fitness.platform.user.dto.AdminStatisticsDto;
import com.fitness.platform.user.service.AdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminStatisticsDto getStatistics() {
        return adminService.getStatistics();
    }
}
