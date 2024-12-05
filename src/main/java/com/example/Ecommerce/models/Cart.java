package com.example.Ecommerce.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Cart {
    @Id
    @GeneratedValue(generator = "uuid")
    private long id;

    @ManyToMany
    private List<Item> items;
    private Double totalAmount;
    public Cart() {
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
    }
}
