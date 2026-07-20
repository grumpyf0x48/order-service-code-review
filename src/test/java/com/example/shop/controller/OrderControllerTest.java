package com.example.shop.controller;

import com.example.shop.entity.Order;
import com.example.shop.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getOrder_whenExists_returns200() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus("PENDING");
        order.setCustomerEmail("customer@example.com");

        when(orderService.getOrder(1L)).thenReturn(order);

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getOrder_whenNotFound_returns404() throws Exception {
        when(orderService.getOrder(99L)).thenReturn(null);

        mockMvc.perform(get("/orders/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createOrder_savesAndReturnsOrder() throws Exception {
        Order order = new Order();
        order.setCustomerEmail("new@example.com");

        Order saved = new Order();
        saved.setId(42L);
        saved.setCustomerEmail("new@example.com");
        saved.setStatus("PENDING");

        when(orderService.save(any())).thenReturn(saved);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    void createOrder_withoutEmail_shouldFail() throws Exception {
        Order order = new Order();
        // pas d'email

        when(orderService.save(any())).thenReturn(order);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllOrders_returnsListOf2() throws Exception {
        Order o1 = new Order(); o1.setId(1L);
        Order o2 = new Order(); o2.setId(2L);

        when(orderService.getAllOrders()).thenReturn(List.of(o1, o2));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
