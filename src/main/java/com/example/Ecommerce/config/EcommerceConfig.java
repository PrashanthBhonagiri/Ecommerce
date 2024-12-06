package com.example.Ecommerce.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ecommerce")
public class EcommerceConfig {
    private int nthOrderDiscount;

    public int getNthOrderDiscount() {
        return nthOrderDiscount;
    }

    public void setNthOrderDiscount(int nthOrderDiscount) {
        this.nthOrderDiscount = nthOrderDiscount;
    }
}
