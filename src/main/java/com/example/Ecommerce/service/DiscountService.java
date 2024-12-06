package com.example.Ecommerce.service;

import com.example.Ecommerce.models.DiscountCode;
import com.example.Ecommerce.repository.DiscountCodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DiscountService {
    private final DiscountCodeRepository discountCodeRepository;
    public DiscountService(DiscountCodeRepository discountCodeRepository) {
        this.discountCodeRepository = discountCodeRepository;
    }

    @Transactional
    public DiscountCode generateDiscountCode() {
        DiscountCode discountCode = new DiscountCode();
        discountCode.setCode(UUID.randomUUID().toString());
        discountCode.setDiscountPercentage(10.0); // 10% discount
        discountCode.setIsUsed(false);
        discountCode.setCreatedAt(LocalDateTime.now());
        return discountCodeRepository.save(discountCode);
    }

    public boolean isValidDiscountCode(String code) {
        return discountCodeRepository.findByCode(code)
                .map(discountCode -> !discountCode.getIsUsed())
                .orElse(false);
    }
}
