package com.example.foodordersystem.repository;

import com.example.foodordersystem.model.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByCategory(String category, Pageable pageable);
    List<MenuItem> findByAvailable(Boolean available, Pageable pageable);

    @Query("SELECT m FROM MenuItem m WHERE m.price BETWEEN :minPrice AND :maxPrice")
    List<MenuItem> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                    @Param("maxPrice") BigDecimal maxPrice);

    Page<MenuItem> findByAvailableTrue(Pageable pageable);

    Page<MenuItem> findByCategoryAndAvailableTrue(String category, Pageable pageable);

    @Query("SELECT m FROM MenuItem m WHERE m.price BETWEEN :minPrice AND :maxPrice AND m.available = true")
    Page<MenuItem> findByPriceRangeAndAvailableTrue(@Param("minPrice") BigDecimal minPrice,
                                                    @Param("maxPrice") BigDecimal maxPrice,
                                                    Pageable pageable);

    @Query("SELECT m FROM MenuItem m WHERE m.category = :category AND m.price BETWEEN :minPrice AND :maxPrice AND m.available = true")
    Page<MenuItem> findByCategoryAndPriceRange(@Param("category") String category,
                                               @Param("minPrice") BigDecimal minPrice,
                                               @Param("maxPrice") BigDecimal maxPrice,
                                               Pageable pageable);

    @Query("SELECT m FROM MenuItem m WHERE " +
            "(LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(m.category) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "m.available = true")
    Page<MenuItem> findBySearchCriteria(@Param("search") String search, Pageable pageable);

    List<MenuItem> findByNameContainingIgnoreCase(String searchTerm);

    Page<MenuItem> findByCategoryAndAvailable(String category, Boolean available, Pageable pageable);

    @Query("SELECT DISTINCT m.category FROM MenuItem m")
    List<String> findAllCategories();
}
