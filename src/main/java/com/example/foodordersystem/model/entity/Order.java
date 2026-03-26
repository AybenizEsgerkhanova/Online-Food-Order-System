package com.example.foodordersystem.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "orderItems")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal totalAmount;

    @Column(unique = true)
    private String idempotencyKey;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems=new ArrayList<>();

    public List<OrderItem> getOrderItems() {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }
        return orderItems;
    }

    public enum OrderStatus {
        PENDING, CONFIRMED, PREPARING, READY, DELIVERED, CANCELLED;


        private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
                PENDING,    Set.of(CONFIRMED, CANCELLED),
                CONFIRMED,  Set.of(PREPARING, CANCELLED),
                PREPARING,  Set.of(READY, CANCELLED),
                READY,      Set.of(DELIVERED),
                DELIVERED,  Set.of(),
                CANCELLED,  Set.of()
        );

        public boolean canTransitionTo(OrderStatus target) {
            return ALLOWED_TRANSITIONS.getOrDefault(this, Set.of()).contains(target);
        }
    }
    public Order() {
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
        this.totalAmount = BigDecimal.ZERO;
    }
    public Order(User user) {
        this();
        this.user = user;
    }

    public void addOrderItem(OrderItem orderItem) {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }
        orderItems.add(orderItem);
        orderItem.setOrder(this);
        calculateTotalAmount();
    }

    // Remove order item method
    public void removeOrderItem(OrderItem orderItem) {
        if (orderItems != null) {
            orderItems.remove(orderItem);
            orderItem.setOrder(null);
            calculateTotalAmount();
        }
    }

    // Calculate total amount
    public void calculateTotalAmount() {
        if (orderItems == null || orderItems.isEmpty()) {
            this.totalAmount = BigDecimal.ZERO;
            return;
        }

        this.totalAmount = orderItems.stream()
                .map(item -> {
                    if (item != null && item.getSubtotal() != null) {
                        return item.getSubtotal();
                    }
                    return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Convenience method to add menu item directly
    public OrderItem addMenuItem(MenuItem menuItem, int quantity) {
        OrderItem orderItem = new OrderItem(menuItem, quantity);
        addOrderItem(orderItem);
        return orderItem;
    }
}

