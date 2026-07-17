package com.example.shop.service;

import com.example.shop.entity.Order;
import com.example.shop.entity.OrderItem;
import com.example.shop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private OrderRepository orderRepository;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void getSalesByCustomer_computesTotalCorrectly() {
        orderRepository.deleteAll();

        Order order = new Order();
        order.setCustomerEmail("client@example.com");
        order.setStatus("CONFIRMED");

        OrderItem item1 = new OrderItem();
        item1.setProductName("Produit A");
        item1.setQuantity(3);
        item1.setPrice(10.0);

        OrderItem item2 = new OrderItem();
        item2.setProductName("Produit B");
        item2.setQuantity(1);
        item2.setPrice(50.0);

        order.setItems(Arrays.asList(item1, item2));
        orderRepository.save(order);

        Map<String, Double> result = reportService.getSalesByCustomer();

        assertEquals(80.0, result.get("client@example.com"));
    }

    @Test
    void isVipCustomer_withMoreThan5ConfirmedOrders_returnsTrue() {
        orderRepository.deleteAll();
        String email = "vip@example.com";

        for (int i = 0; i < 6; i++) {
            Order o = new Order();
            o.setCustomerEmail(email);
            o.setStatus("CONFIRMED");
            orderRepository.save(o);
        }

        assertTrue(reportService.isVipCustomer(email));
    }

    @Test
    void isVipCustomer_withPendingOrders_returnsFalse() {
        String email = "notVip@example.com"; // réutilise l'état de la base précédente ?

        Order o = new Order();
        o.setCustomerEmail(email);
        o.setStatus("PENDING");
        orderRepository.save(o);

        assertFalse(reportService.isVipCustomer(email));
    }

    @Test
    void getConfirmedOrdersForCustomer_filtersCorrectly() {
        String email = "filter@example.com";

        Order confirmed = new Order();
        confirmed.setCustomerEmail(email);
        confirmed.setStatus("CONFIRMED");
        orderRepository.save(confirmed);

        Order pending = new Order();
        pending.setCustomerEmail(email);
        pending.setStatus("PENDING");
        orderRepository.save(pending);

        var results = reportService.getConfirmedOrdersForCustomer(email);

        assertEquals(1, results.size());
        assertEquals("CONFIRMED", results.get(0).getStatus());
    }
}
