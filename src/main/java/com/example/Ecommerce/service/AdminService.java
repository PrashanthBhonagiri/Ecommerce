package com.example.Ecommerce.service;

import com.example.Ecommerce.config.EcommerceConfig;
import com.example.Ecommerce.dto.AdminStats;
import com.example.Ecommerce.models.DiscountCode;
import com.example.Ecommerce.models.Order;
import com.example.Ecommerce.repository.DiscountCodeRepository;
import com.example.Ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private final OrderRepository orderRepository;
    private final DiscountCodeRepository discountCodeRepository;
    private final EcommerceConfig ecommerceConfig;
    private final DiscountService discountService;

    @Autowired
    public AdminService(OrderRepository orderRepository, DiscountCodeRepository discountCodeRepository, EcommerceConfig ecommerceConfig, DiscountService discountService) {
        this.orderRepository = orderRepository;
        this.discountCodeRepository = discountCodeRepository;
        this.ecommerceConfig = ecommerceConfig;
        this.discountService = discountService;
    }

    public AdminStats getAdminStats() {
        AdminStats  stats = new AdminStats();

        List<Order> allOrders = orderRepository.findAll();

        // Calculate total items purchased and total purchase amount
        stats.setTotalItemsPurchased(allOrders.stream()
                .mapToLong(order -> order.getItems().size())
                .sum());

        stats.setTotalPurchaseAmount(allOrders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum());

        // Get all discount codes
        List<DiscountCode> allDiscountCodes = discountCodeRepository.findAll();
        stats.setDiscountCodes(allDiscountCodes.stream()
                .map(DiscountCode::getCode)
                .collect(Collectors.toList()));

        // Calculate total discount amount
        stats.setTotalDiscountAmount(allOrders.stream()
                .filter(order -> order.getAppliedDiscountCode() != null)
                .mapToDouble(order -> {
                    DiscountCode discountCode = order.getAppliedDiscountCode();
                    double originalAmount = order.getTotalAmount() / (1 - discountCode.getDiscountPercentage() / 100);
                    return originalAmount - order.getTotalAmount();
                })
                .sum());

        return stats;
    }
    public DiscountCode generateDiscountCode() {
        if (isNthOrder()) {
            return discountService.generateDiscountCode();
        } else {
            throw new IllegalStateException("Discount code can only be generated for every " + ecommerceConfig.getNthOrderDiscount() + "th order.");
        }
    }

    private boolean isNthOrder() {
        long orderCount = orderRepository.count();
        if (orderCount == 0) {
            return false;
        }
        return (orderCount + 1) % ecommerceConfig.getNthOrderDiscount() == 0;
    }
}
