package com.example.Ecommerce.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class DiscountCode {
    @Id
    @GeneratedValue(generator = "uuid")
    private Long id;
    private String code;
    private Double discountPercentage;
    private Boolean isUsed = false;
    private LocalDateTime createdAt;
    private LocalDateTime usedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
