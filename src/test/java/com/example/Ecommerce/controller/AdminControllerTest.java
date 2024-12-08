package com.example.Ecommerce.controller;

import com.example.Ecommerce.dto.AdminStats;
import com.example.Ecommerce.models.DiscountCode;
import com.example.Ecommerce.service.AdminService;
import com.example.Ecommerce.service.DiscountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AdminControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    void generateDiscountCode_shouldReturnDiscountCode() throws Exception {
        DiscountCode discountCode = new DiscountCode();
        discountCode.setCode("TEST10");
        discountCode.setDiscountPercentage(10.0);

        when(adminService.generateDiscountCode()).thenReturn(discountCode);

        mockMvc.perform(post("/api/admin/generate-discount")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("TEST10"))
                .andExpect(jsonPath("$.discountPercentage").value(10.0));

        verify(adminService, times(1)).generateDiscountCode();
    }

    @Test
    void generateDiscountCode_whenIllegalState_shouldReturnBadRequest() throws Exception {
        when(adminService.generateDiscountCode()).thenThrow(new IllegalStateException("Cannot generate discount code"));

        mockMvc.perform(post("/api/admin/generate-discount")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(adminService, times(1)).generateDiscountCode();
    }

    @Test
    void getAdminStats_shouldReturnAdminStats() throws Exception {
        AdminStats adminStats = new AdminStats();
        adminStats.setTotalItemsPurchased(10L);
        adminStats.setTotalPurchaseAmount(1000.0);

        when(adminService.getAdminStats()).thenReturn(adminStats);

        mockMvc.perform(get("/api/admin/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItemsPurchased").value(10))
                .andExpect(jsonPath("$.totalPurchaseAmount").value(1000.0));

        verify(adminService, times(1)).getAdminStats();
    }
}
