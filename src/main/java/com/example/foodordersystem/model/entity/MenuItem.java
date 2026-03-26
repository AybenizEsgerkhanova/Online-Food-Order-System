package com.example.foodordersystem.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "menu_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    private String category;
    private boolean available = true;

    public MenuItem(String name, String description, BigDecimal price, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.available = true;
    }
}