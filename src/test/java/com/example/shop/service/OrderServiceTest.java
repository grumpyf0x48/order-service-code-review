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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void testSaveOrder() {
        Order order = new Order();
        order.setCustomerEmail("test@example.com");
        order.setStatus("PENDING");

        OrderItem item = new OrderItem();
        item.setProductName("Widget");
        item.setQuantity(2);
        item.setPrice(19.99);
        order.setItems(Arrays.asList(item));

        Order saved = orderService.save(order);

        assertNotNull(saved.getId());
        assertEquals("PENDING", saved.getStatus());
    }

    @Test
    void testGetOrder() {
        Order order = new Order();
        order.setCustomerEmail("test2@example.com");
        order.setStatus("PENDING");
        orderRepository.save(order);

        Order found = orderService.getOrder(order.getId());
        assertNotNull(found);
    }

    @Test
    void testGetAllOrders_returnsAllOrders() {
        orderRepository.deleteAll();

        Order o1 = new Order();
        o1.setCustomerEmail("a@example.com");
        o1.setStatus("PENDING");
        orderRepository.save(o1);

        Order o2 = new Order();
        o2.setCustomerEmail("b@example.com");
        o2.setStatus("CONFIRMED");
        orderRepository.save(o2);

        List<Order> orders = orderService.getAllOrders();

        assertEquals(2, orders.size());
    }

    @Test
    void testDeleteOrder() {
        Order order = new Order();
        order.setCustomerEmail("delete@example.com");
        order.setStatus("PENDING");
        Order saved = orderRepository.save(order);

        orderService.delete(saved.getId());

        assertTrue(orderRepository.findById(saved.getId()).isEmpty());
    }
}
