package com.example.foodordersystem.repository;

import com.example.foodordersystem.model.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long>, JpaSpecificationExecutor<MenuItem> {
    List<MenuItem> findByCategory(String category, Pageable pageable);
    List<MenuItem> findByAvailable(Boolean available, Pageable pageable);

    @Query("SELECT m FROM MenuItem m WHERE m.price BETWEEN :minPrice AND :maxPrice")
    List<MenuItem> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                    @Param("maxPrice") BigDecimal maxPrice);

    List<MenuItem> findByNameContainingIgnoreCase(String searchTerm);

    Page<MenuItem> findByCategoryAndAvailable(String category, Boolean available, Pageable pageable);

    @Query("SELECT DISTINCT m.category FROM MenuItem m")
    List<String> findAllCategories();
}
