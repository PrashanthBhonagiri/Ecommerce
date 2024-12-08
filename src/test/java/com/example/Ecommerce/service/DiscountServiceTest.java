package com.example.Ecommerce.service;

import com.example.Ecommerce.exception.DiscountCodeAlreadyUsedException;
import com.example.Ecommerce.exception.InvalidDiscountCodeException;
import com.example.Ecommerce.models.DiscountCode;
import com.example.Ecommerce.models.Order;
import com.example.Ecommerce.repository.DiscountCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DiscountServiceTest {
    @Mock
    private DiscountCodeRepository discountCodeRepository;

    @InjectMocks
    private DiscountService discountService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateDiscountCode_shouldCreateAndSaveDiscountCode() {
        DiscountCode discountCode = new DiscountCode();
        discountCode.setCode(UUID.randomUUID().toString());
        discountCode.setDiscountPercentage(10.0);
        discountCode.setIsUsed(false);
        discountCode.setCreatedAt(LocalDateTime.now());

        when(discountCodeRepository.save(any(DiscountCode.class))).thenReturn(discountCode);

        DiscountCode generatedCode = discountService.generateDiscountCode();

        assertNotNull(generatedCode);
        assertEquals(10.0, generatedCode.getDiscountPercentage());
        assertFalse(generatedCode.getIsUsed());
        assertNotNull(generatedCode.getCreatedAt());
        verify(discountCodeRepository, times(1)).save(any(DiscountCode.class));
    }

    @Test
    void isValidDiscountCode_withValidUnusedCode_shouldReturnTrue() {
        String code = "VALIDCODE";
        DiscountCode discountCode = new DiscountCode();
        discountCode.setIsUsed(false);
        when(discountCodeRepository.findByCode(code)).thenReturn(Optional.of(discountCode));

        boolean isValid = discountService.isValidDiscountCode(code);

        assertTrue(isValid);
        verify(discountCodeRepository, times(1)).findByCode(code);
    }

    @Test
    void isValidDiscountCode_withUsedCode_shouldReturnFalse() {
        String code = "USEDCODE";
        DiscountCode discountCode = new DiscountCode();
        discountCode.setIsUsed(true);
        when(discountCodeRepository.findByCode(code)).thenReturn(Optional.of(discountCode));

        boolean isValid = discountService.isValidDiscountCode(code);

        assertFalse(isValid);
        verify(discountCodeRepository, times(1)).findByCode(code);
    }

    @Test
    void isValidDiscountCode_withNonExistentCode_shouldReturnFalse() {
        String code = "NONEXISTENT";
        when(discountCodeRepository.findByCode(code)).thenReturn(Optional.empty());

        boolean isValid = discountService.isValidDiscountCode(code);

        assertFalse(isValid);
        verify(discountCodeRepository, times(1)).findByCode(code);
    }

    @Test
    void applyDiscount_withValidUnusedCode_shouldApplyDiscount() throws DiscountCodeAlreadyUsedException, InvalidDiscountCodeException {
        String code = "VALIDCODE";
        DiscountCode discountCode = new DiscountCode();
        discountCode.setCode(code);
        discountCode.setDiscountPercentage(10.0);
        discountCode.setIsUsed(false);

        Order order = new Order();
        order.setTotalAmount(100.0);

        when(discountCodeRepository.findByCode(code)).thenReturn(Optional.of(discountCode));

        discountService.applyDiscount(order, code);

        assertEquals(90.0, order.getTotalAmount());
        assertTrue(discountCode.getIsUsed());
        assertNotNull(discountCode.getUsedAt());
        assertEquals(discountCode, order.getAppliedDiscountCode());
        verify(discountCodeRepository, times(1)).findByCode(code);
        verify(discountCodeRepository, times(1)).save(discountCode);
    }

    @Test
    void applyDiscount_withInvalidCode_shouldThrowException() {
        String code = "INVALIDCODE";
        Order order = new Order();
        when(discountCodeRepository.findByCode(code)).thenReturn(Optional.empty());

        assertThrows(InvalidDiscountCodeException.class, () -> discountService.applyDiscount(order, code));
        verify(discountCodeRepository, times(1)).findByCode(code);
        verify(discountCodeRepository, never()).save(any(DiscountCode.class));
    }

    @Test
    void applyDiscount_withUsedCode_shouldThrowException() {
        String code = "USEDCODE";
        DiscountCode discountCode = new DiscountCode();
        discountCode.setCode(code);
        discountCode.setIsUsed(true);

        Order order = new Order();
        when(discountCodeRepository.findByCode(code)).thenReturn(Optional.of(discountCode));

        assertThrows(DiscountCodeAlreadyUsedException.class, () -> discountService.applyDiscount(order, code));
        verify(discountCodeRepository, times(1)).findByCode(code);
        verify(discountCodeRepository, never()).save(any(DiscountCode.class));
    }
}
