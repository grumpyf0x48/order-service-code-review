package com.example.shop.service;

import com.example.shop.entity.Order;
import com.example.shop.entity.OrderItem;
import com.example.shop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Retourne le chiffre d'affaires total par client.
     * Utilisé par le dashboard principal, appelé à chaque chargement de page.
     */
    public Map<String, Double> getSalesByCustomer() {
        List<Order> orders = orderRepository.findAll();
        Map<String, Double> result = new HashMap<>();

        for (Order order : orders) {
            double total = 0;
            for (OrderItem item : order.getItems()) {
                total += item.getPrice() * item.getQuantity();
            }
            result.merge(order.getCustomerEmail(), total, Double::sum);
        }

        return result;
    }

    /**
     * Retourne les commandes confirmées d'un client donné.
     */
    public List<Order> getConfirmedOrdersForCustomer(String email) {
        List<Order> orders = orderRepository.findByCustomerEmail(email);
        orders.removeIf(o -> !o.getStatus().equals("CONFIRMED"));
        return orders;
    }

    /**
     * Vérifie si un client est "VIP" : plus de 5 commandes confirmées.
     */
    public boolean isVipCustomer(String email) {
        List<Order> orders = orderRepository.findByCustomerEmail(email);
        long count = orders.stream()
                .filter(o -> o.getStatus().equals("CONFIRMED"))
                .count();
        return count > 5;
    }
}
