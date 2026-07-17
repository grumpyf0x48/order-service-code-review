package com.example.shop.repository;

import com.example.shop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Trouve les commandes par statut
    List<Order> findByStatus(String status);

    // Trouve les commandes d'un client
    @Query("SELECT o FROM Order o WHERE o.customerEmail = :email")
    List<Order> findByCustomerEmail(@Param("email") String email);

    // Rapport : total des ventes par client (utilisé dans le dashboard)
    @Query("SELECT o.customerEmail, SUM(i.price * i.quantity) " +
           "FROM Order o JOIN o.items i " +
           "GROUP BY o.customerEmail")
    List<Object[]> getSalesSummaryByCustomer();
}
