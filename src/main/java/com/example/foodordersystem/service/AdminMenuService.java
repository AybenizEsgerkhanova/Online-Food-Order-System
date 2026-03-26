package com.example.foodordersystem.service;

import com.example.foodordersystem.model.dto.MenuItemDTO;
import com.example.foodordersystem.model.dto.request.BulkAvailabilityRequest;
import com.example.foodordersystem.model.dto.request.MenuItemRequest;
import com.example.foodordersystem.model.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;
import java.util.List;

public interface AdminMenuService {

    MenuItemDTO createMenuItem(@Valid MenuItemRequest request, String username);

    MenuItem updateMenuItem(Long id, @Valid MenuItemRequest request, String username);

    void deleteMenuItem(Long id, String username);

    MenuItem toggleAvailability(Long id, boolean available, String username);

    Page<MenuItem> getAllMenuItemsForAdmin(Pageable pageable, String category, Boolean available, String username);

    List<String> getAllCategories();

    List<MenuItem> bulkUpdateAvailability(List<BulkAvailabilityRequest> requests, String username);
}
