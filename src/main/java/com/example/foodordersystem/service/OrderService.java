package com.example.foodordersystem.service;

import com.example.foodordersystem.model.dto.request.OrderRequest;
import com.example.foodordersystem.model.dto.response.OrderResponse;
import com.example.foodordersystem.model.entity.Order;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderRequest orderRequest, String username, String idempotencyKey);
    OrderResponse getOrderById(Long id, String username);
    List<OrderResponse> getUserOrders(String username);
    OrderResponse updateOrderStatus(Long id, String status, String username);
    List<OrderResponse> getAllOrders(String username);
}
