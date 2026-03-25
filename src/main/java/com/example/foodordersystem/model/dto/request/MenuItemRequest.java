package com.example.foodordersystem.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Menyu maddəsi yaratmaq və ya yeniləmək üçün request")
public class MenuItemRequest {

    @NotBlank(message = "Menyu adı boş ola bilməz")
    @Size(min = 2, max = 100, message = "Menyu adı 2-100 simvol arasında olmalıdır")
    @Schema(description = "Menyu maddəsinin adı", example = "Margarita Pizza")
    private String name;

    @NotBlank(message = "Təsvir boş ola bilməz")
    @Size(min = 10, max = 500, message = "Təsvir 10-500 simvol arasında olmalıdır")
    @Schema(description = "Menyu maddəsinin təsviri", example = "Klassik domates sousu və mozzarella pendiri")
    private String description;

    @NotNull(message = "Qiymət boş ola bilməz")
    @DecimalMin(value = "0.01", message = "Qiymət 0.01-dən böyük olmalıdır")
    @DecimalMax(value = "999.99", message = "Qiymət 999.99-dan kiçik olmalıdır")
    @Digits(integer = 8, fraction = 2, message = "Qiymət maksimum 2 onluq rəqəm ola bilər")
    @Schema(description = "Menyu maddəsinin qiyməti", example = "12.99")
    private BigDecimal price;

    @NotBlank(message = "Kateqoriya boş ola bilməz")
    @Pattern(regexp = "^(Pizza|Pasta|Salad|Beverage|Dessert|Appetizer)$",
            message = "Kateqoriya yalnız Pizza, Pasta, Salad, Beverage, Dessert, Appetizer ola bilər")
    @Schema(description = "Menyu kateqoriyası", example = "Pizza")
    private String category;

    @Schema(description = "Menyu maddəsinin mövcudluq statusu", example = "true")
    private boolean available = true;

    @Size(max = 255, message = "Şəkil URL-i 255 simvoldan çox ola bilməz")
    @Pattern(regexp = "^(https?://.*\\.(jpg|jpeg|png|gif|webp))?$",
            message = "Düzgün şəkil URL formatı daxil edin")
    @Schema(description = "Menyu maddəsinin şəkil URL-i", example = "https://example.com/pizza.jpg")
    private String imageUrl;
}
