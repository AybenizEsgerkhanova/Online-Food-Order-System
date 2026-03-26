package com.example.foodordersystem.controller;

import com.example.foodordersystem.model.dto.MenuItemDTO;
import com.example.foodordersystem.model.dto.request.MenuItemRequest;
import com.example.foodordersystem.model.dto.response.ErrorResponse;
import com.example.foodordersystem.model.entity.MenuItem;
import com.example.foodordersystem.service.AdminMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.foodordersystem.model.dto.request.AvailabilityRequest;
import com.example.foodordersystem.model.dto.request.BulkAvailabilityRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/menu")
@Tag(name = "Admin Menu Management", description = "Admin mənyunu idarə etmək üçün endpoint-lər")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminMenuController {

    private final AdminMenuService adminMenuService;

    public AdminMenuController(AdminMenuService adminMenuService) {
        this.adminMenuService = adminMenuService;
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Yeni menyu maddəsi əlavə et",
            description = "Admin və ya staff yeni menyu maddəsi əlavə edə bilər")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Menyu maddəsi uğurla yaradıldı",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MenuItem.class))),
            @ApiResponse(responseCode = "400", description = "Yanlış məlumatlar",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Bu əməliyyat üçün icazəniz yoxdur"),
            @ApiResponse(responseCode = "409", description = "Bu adda menyu maddəsi artıq mövcuddur")
    })
    public ResponseEntity<MenuItemDTO> createMenuItem(
            @Valid @RequestBody @Parameter(description = "Menyu maddəsi məlumatları") MenuItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        MenuItemDTO createdItem = adminMenuService.createMenuItem(request, userDetails.getUsername());
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Menyu maddəsini yenilə",
            description = "Mövcud menyu maddəsinin məlumatlarını yeniləyir")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menyu maddəsi uğurla yeniləndi"),
            @ApiResponse(responseCode = "404", description = "Menyu maddəsi tapılmadı"),
            @ApiResponse(responseCode = "400", description = "Yanlış məlumatlar"),
            @ApiResponse(responseCode = "403", description = "Bu əməliyyat üçün icazəniz yoxdur")
    })
    public ResponseEntity<MenuItem> updateMenuItem(
            @PathVariable @Parameter(description = "Menyu maddəsi ID-si") Long id,
            @Valid @RequestBody @Parameter(description = "Yenilənmiş menyu məlumatları") MenuItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        MenuItem updatedItem = adminMenuService.updateMenuItem(id, request, userDetails.getUsername());
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Menyu maddəsini sil",
            description = "Yalnız admin menyu maddəsini silə bilər")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Menyu maddəsi uğurla silindi"),
            @ApiResponse(responseCode = "404", description = "Menyu maddəsi tapılmadı"),
            @ApiResponse(responseCode = "403", description = "Bu əməliyyat üçün icazəniz yoxdur"),
            @ApiResponse(responseCode = "409", description = "Bu menyu maddəsi sifarişlərdə istifadə olunur")
    })
    public ResponseEntity<Void> deleteMenuItem(
            @PathVariable @Parameter(description = "Menyu maddəsi ID-si") Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        adminMenuService.deleteMenuItem(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Menyu maddəsinin mövcudluğunu dəyiş",
            description = "Menyu maddəsini aktiv/deaktiv edir")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mövcudluq statusu dəyişdirildi"),
            @ApiResponse(responseCode = "404", description = "Menyu maddəsi tapılmadı"),
            @ApiResponse(responseCode = "403", description = "Bu əməliyyat üçün icazəniz yoxdur")
    })
    public ResponseEntity<MenuItem> toggleAvailability(
            @PathVariable @Parameter(description = "Menyu maddəsi ID-si") Long id,
            @RequestBody @Parameter(description = "Yeni mövcudluq statusu") AvailabilityRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        MenuItem updatedItem = adminMenuService.toggleAvailability(id, request.isAvailable(), userDetails.getUsername());
        return ResponseEntity.ok(updatedItem);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Bütün menyu maddələrini əldə et (admin görünümü)",
            description = "Deaktiv maddələr də daxil olmaqla bütün menyu maddələrini qaytarır")
    public ResponseEntity<Page<MenuItem>> getAllMenuItemsForAdmin(
            @RequestParam(defaultValue = "0") @Parameter(description = "Səhifə nömrəsi") int page,
            @RequestParam(defaultValue = "20") @Parameter(description = "Səhifə ölçüsü") int size,
            @RequestParam(defaultValue = "name") @Parameter(description = "Sıralama sahəsi") String sortBy,
            @RequestParam(defaultValue = "asc") @Parameter(description = "Sıralama istiqaməti") String sortDir,
            @RequestParam(required = false) @Parameter(description = "Kateqoriya filtri") String category,
            @RequestParam(required = false) @Parameter(description = "Mövcudluq filtri") Boolean available,
            @AuthenticationPrincipal UserDetails userDetails) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<MenuItem> menuItems = adminMenuService.getAllMenuItemsForAdmin(
                pageable, category, available, userDetails.getUsername());

        return ResponseEntity.ok(menuItems);
    }

    @GetMapping("/categories")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Mövcud kateqoriyaların siyahısı",
            description = "Sistemdə mövcud bütün menyu kateqoriyalarını qaytarır")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = adminMenuService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/bulk-update-availability")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toplu mövcudluq yeniləməsi",
            description = "Bir neçə menyu maddəsinin mövcudluq statusunu eyni vaxtda dəyişir")
    public ResponseEntity<List<MenuItem>> bulkUpdateAvailability(
            @RequestBody @Parameter(description = "ID-lər və yeni status")
            List<BulkAvailabilityRequest> requests,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<MenuItem> updatedItems = adminMenuService.bulkUpdateAvailability(requests, userDetails.getUsername());
        return ResponseEntity.ok(updatedItems);
    }

}

