package com.example.Ecommerce.controller;

import com.example.Ecommerce.models.DiscountCode;
import com.example.Ecommerce.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final DiscountService discountService;

    @Autowired
    public AdminController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @PostMapping("/generate-discount")
    public ResponseEntity<DiscountCode> generateDiscountCode() {
        DiscountCode discountCode = discountService.generateDiscountCode();
        return ResponseEntity.ok(discountCode);
    }

}
