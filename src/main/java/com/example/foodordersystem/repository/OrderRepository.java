package com.example.foodordersystem.repository;

import com.example.foodordersystem.model.entity.Order;
import com.example.foodordersystem.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByStatus(String status);
    @Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
    List<Order> findAllOrderByOrderDateDesc();

    List<Object> findByUser(User user);

    Optional<Order> findByIdempotencyKey(String idempotencyKey);
}