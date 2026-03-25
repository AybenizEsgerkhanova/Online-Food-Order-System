package com.example.foodordersystem.controller;

import com.example.foodordersystem.model.dto.request.OrderRequest;
import com.example.foodordersystem.model.dto.response.OrderResponse;
import com.example.foodordersystem.service.OrderServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderServiceImpl orderService;

    public OrderController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody OrderRequest orderRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        OrderResponse order = orderService.createOrder(orderRequest, userDetails.getUsername(), idempotencyKey);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        OrderResponse order = orderService.getOrderById(id, userDetails.getUsername());
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public List<OrderResponse> getUserOrders(@AuthenticationPrincipal UserDetails userDetails) {
        return orderService.getUserOrders(userDetails.getUsername());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @AuthenticationPrincipal UserDetails userDetails) {

        OrderResponse order = orderService.updateOrderStatus(id, status, userDetails.getUsername());
        return ResponseEntity.ok(order);
    }

    @GetMapping("/all")
    public List<OrderResponse> getAllOrders(@AuthenticationPrincipal UserDetails userDetails) {
        return orderService.getAllOrders(userDetails.getUsername());
    }
}