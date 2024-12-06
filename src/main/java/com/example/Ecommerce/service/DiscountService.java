package com.example.Ecommerce.service;

import com.example.Ecommerce.exception.DiscountCodeAlreadyUsedException;
import com.example.Ecommerce.exception.InvalidDiscountCodeException;
import com.example.Ecommerce.models.DiscountCode;
import com.example.Ecommerce.models.Order;
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

    public void applyDiscount(Order order, String discountCode) throws DiscountCodeAlreadyUsedException, InvalidDiscountCodeException {

        DiscountCode discount = discountCodeRepository.findByCode(discountCode)
                .orElseThrow(() -> new InvalidDiscountCodeException("Invalid discount code: " + discountCode));

        if (discount.getIsUsed()) {
            throw new DiscountCodeAlreadyUsedException("Discount code has already been used: " + discountCode);
        }

        double discountAmount = order.getTotalAmount() * (discount.getDiscountPercentage() / 100);
        order.setTotalAmount(order.getTotalAmount() - discountAmount);
        order.setAppliedDiscountCode(discount);

        discount.setIsUsed(true);
        discount.setUsedAt(LocalDateTime.now());

        discountCodeRepository.save(discount);
    }


}
