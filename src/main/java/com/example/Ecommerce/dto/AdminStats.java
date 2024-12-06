package com.example.Ecommerce.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdminStats {
    private long totalItemsPurchased;
    private double totalPurchaseAmount;
    private List<String> discountCodes;
    private double totalDiscountAmount;
}
