package com.example.foodordersystem.service;

import com.example.foodordersystem.model.entity.MenuItem;
import com.example.foodordersystem.repository.MenuItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MenuServiceImpl implements MenuService {
    private final MenuItemRepository menuItemRepository;
    public MenuServiceImpl(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }


    @Override
    public Page<MenuItem> getAllMenuItems(Pageable pageable, String category,
                                          BigDecimal minPrice, BigDecimal maxPrice, String search) {

        if (search != null && !search.trim().isEmpty()) {
            return menuItemRepository.findBySearchCriteria(search, pageable);
        }

        if (category != null && minPrice != null && maxPrice != null) {
            return menuItemRepository.findByCategoryAndPriceRange(category, minPrice, maxPrice, pageable);
        }

        if (category != null) {
            return menuItemRepository.findByCategoryAndAvailableTrue(category, pageable);
        }

        if (minPrice != null && maxPrice != null) {
            return menuItemRepository.findByPriceRangeAndAvailableTrue(minPrice, maxPrice, pageable);
        }

        return menuItemRepository.findByAvailableTrue(pageable);
    }
@Override
@Cacheable(value = "menuItems", key = "#id")
    public MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
    }
@Override
    public List<MenuItem> getMenuItemsByCategory(String category,Pageable pageable) {
        return menuItemRepository.findByCategory(category, pageable);
    }
@Override
    public List<MenuItem> getMenuItemsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return menuItemRepository.findByPriceRange(minPrice, maxPrice);
    }
@Override
    public List<MenuItem> searchMenuItems(String searchTerm) {
        return menuItemRepository.findByNameContainingIgnoreCase(searchTerm);
    }
}