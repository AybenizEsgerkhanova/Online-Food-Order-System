package com.example.foodordersystem.controller;

import com.example.foodordersystem.model.dto.request.OrderRequest;
import com.example.foodordersystem.model.dto.request.StatusUpdateRequest;
import com.example.foodordersystem.model.dto.response.OrderResponse;
import com.example.foodordersystem.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody OrderRequest orderRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        OrderResponse order = orderService.createOrder(orderRequest, userDetails.getUsername(), idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        OrderResponse order = orderService.getOrderById(id, userDetails.getUsername());
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public List<OrderResponse> getOrders(@AuthenticationPrincipal UserDetails userDetails) {
        return orderService.getOrders(userDetails.getUsername());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        OrderResponse order = orderService.updateOrderStatus(id, request.getStatus(), userDetails.getUsername());
        return ResponseEntity.ok(order);
    }
}
