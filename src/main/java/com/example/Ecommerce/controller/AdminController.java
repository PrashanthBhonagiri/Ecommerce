package com.example.Ecommerce.controller;

import com.example.Ecommerce.dto.AdminStats;
import com.example.Ecommerce.models.DiscountCode;
import com.example.Ecommerce.service.AdminService;
import com.example.Ecommerce.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final DiscountService discountService;
    private final AdminService adminService;

    @Autowired
    public AdminController(DiscountService discountService, AdminService adminService) {
        this.discountService = discountService;
        this.adminService = adminService;
    }

    @PostMapping("/generate-discount")
    public ResponseEntity<DiscountCode> generateDiscountCode() {
        DiscountCode discountCode = discountService.generateDiscountCode();
        return ResponseEntity.ok(discountCode);
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStats> getAdminStats() {
        AdminStats stats = adminService.getAdminStats();
        return ResponseEntity.ok(stats);
    }

}
