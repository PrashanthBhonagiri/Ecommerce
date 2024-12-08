package com.example.Ecommerce.service;

import com.example.Ecommerce.config.EcommerceConfig;
import com.example.Ecommerce.dto.AdminStats;
import com.example.Ecommerce.models.DiscountCode;
import com.example.Ecommerce.models.Item;
import com.example.Ecommerce.models.Order;
import com.example.Ecommerce.repository.DiscountCodeRepository;
import com.example.Ecommerce.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private DiscountCodeRepository discountCodeRepository;
    @Mock
    private DiscountService discountService;
    @Mock
    private EcommerceConfig ecommerceConfig;
    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAdminStats_shouldCalculateCorrectStats() {
        Order order1 = new Order();
        order1.setItems(new HashSet<>(Arrays.asList(new Item(), new Item())));
        order1.setTotalAmount(100.0);

        Order order2 = new Order();
        order2.setItems(new HashSet<>(Arrays.asList(new Item())));
        order2.setTotalAmount(50.0);

        DiscountCode discountCode1 = new DiscountCode();
        discountCode1.setCode("DISCOUNT10");
        discountCode1.setDiscountPercentage(10.0);
        order2.setAppliedDiscountCode(discountCode1);

        DiscountCode discountCode2 = new DiscountCode();
        discountCode2.setCode("DISCOUNT20");

        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));
        when(discountCodeRepository.findAll()).thenReturn(Arrays.asList(discountCode1, discountCode2));

        AdminStats stats = adminService.getAdminStats();

        assertEquals(3, stats.getTotalItemsPurchased());
        assertEquals(150.0, stats.getTotalPurchaseAmount());
        assertEquals(Arrays.asList("DISCOUNT10", "DISCOUNT20"), stats.getDiscountCodes());
        assertEquals(5.55, stats.getTotalDiscountAmount(), 0.01);

        verify(orderRepository, times(1)).findAll();
        verify(discountCodeRepository, times(1)).findAll();
    }

    @Test
    void generateDiscountCode_whenNthOrder_shouldGenerateCode() {
        when(orderRepository.count()).thenReturn(4L);
        when(ecommerceConfig.getNthOrderDiscount()).thenReturn(5);
        DiscountCode expectedDiscountCode = new DiscountCode();
        when(discountService.generateDiscountCode()).thenReturn(expectedDiscountCode);

        DiscountCode generatedCode = adminService.generateDiscountCode();

        assertNotNull(generatedCode);
        assertEquals(expectedDiscountCode, generatedCode);
        verify(orderRepository, times(1)).count();
        verify(ecommerceConfig, times(1)).getNthOrderDiscount();
        verify(discountService, times(1)).generateDiscountCode();
    }

    @Test
    void generateDiscountCode_whenNotNthOrder_shouldThrowException() {
        when(orderRepository.count()).thenReturn(3L);
        when(ecommerceConfig.getNthOrderDiscount()).thenReturn(5);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> adminService.generateDiscountCode());
        assertEquals("Discount code can only be generated for every 5 order.", exception.getMessage());

        verify(orderRepository, times(1)).count();
        verify(ecommerceConfig, times(2)).getNthOrderDiscount();
        verify(discountService, never()).generateDiscountCode();
    }

    @Test
    void generateDiscountCode_whenNoOrders_shouldThrowException() {
        when(orderRepository.count()).thenReturn(0L);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> adminService.generateDiscountCode());
        assertEquals("Discount code can only be generated for every " + ecommerceConfig.getNthOrderDiscount() + " order.", exception.getMessage());

        verify(orderRepository, times(1)).count();
        verify(discountService, never()).generateDiscountCode();
    }
}
