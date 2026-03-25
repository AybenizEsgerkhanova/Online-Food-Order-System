package com.example.foodordersystem.repository;

import com.example.foodordersystem.model.entity.Order;
import com.example.foodordersystem.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems LEFT JOIN FETCH o.user WHERE o.user.id = :userId")
    List<Order> findByUserId(@Param("userId") Long userId);
    List<Order> findByStatus(String status);
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems LEFT JOIN FETCH o.user ORDER BY o.orderDate DESC")
    List<Order> findAllOrderByOrderDateDesc();

    List<Object> findByUser(User user);

    Optional<Order> findByIdempotencyKey(String idempotencyKey);
}