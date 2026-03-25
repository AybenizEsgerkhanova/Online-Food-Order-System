package com.example.foodordersystem.controller;

import com.example.foodordersystem.model.entity.MenuItem;
import com.example.foodordersystem.service.MenuServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuServiceImpl menuService;

    public MenuController(MenuServiceImpl menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    @Operation(summary = "Menyu siyahısını əldə et",
            description = "Filtrlər, pagination və sorting ilə menyu maddələrini qaytarır")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menyu siyahısı uğurla qaytarıldı")
    })
    public ResponseEntity<Page<MenuItem>> getAllMenuItems(
            @RequestParam(required = false)
            @Parameter(description = "Kateqoriya filtri") String category,

            @RequestParam(required = false)
            @Parameter(description = "Minimum qiymət") BigDecimal minPrice,

            @RequestParam(required = false)
            @Parameter(description = "Maksimum qiymət") BigDecimal maxPrice,

            @RequestParam(required = false)
            @Parameter(description = "Axtarış sorğusu") String search,

            @RequestParam(defaultValue = "0")
            @Parameter(description = "Səhifə nömrəsi (0-dan başlayır)") int page,

            @RequestParam(defaultValue = "10")
            @Parameter(description = "Səhifə ölçüsü") int size,

            @RequestParam(defaultValue = "name")
            @Parameter(description = "Sıralama sahəsi",
                    schema = @Schema(allowableValues = {"name", "price", "category", "id"})) String sortBy,

            @RequestParam(defaultValue = "asc")
            @Parameter(description = "Sıralama istiqaməti",
                    schema = @Schema(allowableValues = {"asc", "desc"})) String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<MenuItem> menuItems = (Page<MenuItem>) menuService.getAllMenuItems(pageable, category, minPrice, maxPrice, search);

        // Response headers əlavə et
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(menuItems.getTotalElements()));
        headers.add("X-Page-Number", String.valueOf(menuItems.getNumber()));
        headers.add("X-Page-Size", String.valueOf(menuItems.getSize()));
        headers.add("X-Total-Pages", String.valueOf(menuItems.getTotalPages()));

        return ResponseEntity.ok().headers(headers).body(menuItems);
    }


    @GetMapping("/{id}")
    public MenuItem getMenuItem(@PathVariable Long id) {
        return menuService.getMenuItemById(id);
    }
}