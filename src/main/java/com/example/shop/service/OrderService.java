package com.example.shop.service;

import com.example.shop.entity.Order;
import com.example.shop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;

@Service
@Transactional
public class OrderService {

    private static final Logger logger = Logger.getLogger(OrderService.class.getName());

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order save(Order order) {
        Order saved = orderRepository.save(order);

        // Send confirmation email
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(order.getCustomerEmail());
            message.setSubject("Order update - #" + order.getId());
            message.setText("Your order status is now: " + order.getStatus());
            mailSender.send(message);
        } catch (Exception e) {
            logger.warning("Failed to send email: " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }

        return saved;
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }
}
