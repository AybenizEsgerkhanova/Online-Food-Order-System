package com.example.foodordersystem.specification;

import com.example.foodordersystem.model.entity.MenuItem;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class MenuItemSpec {

    public static Specification<MenuItem> withFilters(
            String category, BigDecimal minPrice, BigDecimal maxPrice, String search) {
        return Specification
                .where(availableOnly())
                .and(hasSearch(search))
                .and(hasCategory(category))
                .and(hasPriceRange(minPrice, maxPrice));
    }

    private static Specification<MenuItem> availableOnly() {
        return (root, query, cb) -> cb.isTrue(root.get("available"));
    }

    private static Specification<MenuItem> hasSearch(String search) {
        if (search == null || search.isBlank()) return null;
        String pattern = "%" + search.toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("description")), pattern),
                cb.like(cb.lower(root.get("category")), pattern)
        );
    }

    private static Specification<MenuItem> hasCategory(String category) {
        if (category == null) return null;
        return (root, query, cb) -> cb.equal(root.get("category"), category);
    }

    private static Specification<MenuItem> hasPriceRange(BigDecimal min, BigDecimal max) {
        if (min == null || max == null) return null;
        return (root, query, cb) -> cb.between(root.get("price"), min, max);
    }
}
