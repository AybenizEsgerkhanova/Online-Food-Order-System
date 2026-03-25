package com.example.foodordersystem.service;

import com.example.foodordersystem.exception.MenuItemNotFoundException;
import com.example.foodordersystem.mapper.MenuItemMapper;
import com.example.foodordersystem.model.dto.MenuItemDTO;
import com.example.foodordersystem.model.dto.request.MenuItemRequest;
import com.example.foodordersystem.model.entity.MenuItem;
import com.example.foodordersystem.repository.MenuItemRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Service
@Transactional(readOnly = true)
public class AdminMenuServiceImpl implements AdminMenuService{

    private final MenuItemRepository menuItemRepository;
    private final MenuItemMapper menuItemMapper;

    public AdminMenuServiceImpl(MenuItemRepository menuItemRepository, MenuItemMapper menuItemMapper) {
        this.menuItemRepository = menuItemRepository;
        this.menuItemMapper = menuItemMapper;
    }
@Override
@Transactional
    public MenuItemDTO createMenuItem(@Valid MenuItemRequest request, String username) {
        MenuItem entity = menuItemMapper.toEntity(request);
        entity.setAvailable(true);
        MenuItem saved = menuItemRepository.save(entity);
        return menuItemMapper.toDTO(saved);
    }
@Override
@Transactional
    public MenuItem updateMenuItem(Long id, @Valid MenuItemRequest request, String username) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found"));
        menuItemMapper.updateMenuItemFromRequest(request, menuItem);

        return menuItemRepository.save(menuItem);
    }
    @Override
    @Transactional
    public void deleteMenuItem(Long id, String username) {
        if (!menuItemRepository.existsById(id)) {
            throw new MenuItemNotFoundException("Menu item not found");
        }
        menuItemRepository.deleteById(id);
    }
@Override
@Transactional
    public MenuItem toggleAvailability(Long id, boolean available, String username) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found"));
        menuItem.setAvailable(available);
        return menuItemRepository.save(menuItem);
    }
@Override
    public Page<MenuItem> getAllMenuItemsForAdmin(Pageable pageable, String category, Boolean available, String username) {
        if (category != null && available != null) {
            return menuItemRepository.findByCategoryAndAvailable(category, available, pageable);
        } else if (category != null) {
            @SuppressWarnings("unchecked")
            Page<MenuItem> result = (Page<MenuItem>) menuItemRepository.findByCategory(category, pageable);
            return result;
        } else if (available != null) {
            @SuppressWarnings("unchecked")
            Page<MenuItem> result = (Page<MenuItem>) menuItemRepository.findByAvailable(available, pageable);
            return result;
        }
        return menuItemRepository.findAll(pageable);
    }
@Override
    public List<String> getAllCategories() {
        List<MenuItem> allItems = menuItemRepository.findAll();
        return allItems.stream()
                .map(MenuItem::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }
@Override
@Transactional
    public List<MenuItem> bulkUpdateAvailability(List<com.example.foodordersystem.controller.AdminMenuController.BulkAvailabilityRequest> requests,
                                                 String username) {
        List<Long> ids = requests.stream().map(r -> r.getId()).toList();
        List<MenuItem> items = menuItemRepository.findAllById(ids);

        items.forEach(item -> {
            requests.stream()
                    .filter(r -> r.getId().equals(item.getId()))
                    .findFirst()
                    .ifPresent(r -> item.setAvailable(r.isAvailable()));
        });

        return menuItemRepository.saveAll(items);
    }
}
