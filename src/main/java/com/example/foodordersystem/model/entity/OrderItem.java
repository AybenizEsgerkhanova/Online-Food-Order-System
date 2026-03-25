package com.example.foodordersystem.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "order")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    private Integer quantity;

    private BigDecimal price;
    public OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.price = menuItem.getPrice();
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        if (this.order != null) {
            this.order.calculateTotalAmount();
        }
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
        if (this.order != null) {
            this.order.calculateTotalAmount();
        }
    }


    public BigDecimal getSubtotal() {
        if (price == null || quantity == 0) {
            return BigDecimal.ZERO;
        }
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public void updatePriceFromMenuItem() {
        if (menuItem != null) {
            this.price = menuItem.getPrice();
        }
    }
}