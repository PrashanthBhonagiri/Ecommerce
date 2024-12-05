package com.example.Ecommerce.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Order {
    @Id
    @GeneratedValue(generator = "uuid")
    private Long id;
    @ManyToMany
    private List<Item> items;
    private Double totalAmount;
    private LocalDateTime orderDate;
    @ManyToOne
    private DiscountCode appliedDiscountCode;

    public Order() {
        this.items = new ArrayList<>();
        this.orderDate = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.orderDate == null) {
            this.orderDate = LocalDateTime.now();
        }
    }


}
