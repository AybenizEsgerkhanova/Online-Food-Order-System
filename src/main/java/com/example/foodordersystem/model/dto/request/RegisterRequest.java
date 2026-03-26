package com.example.foodordersystem.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "İstifadəçi qeydiyyat məlumatları")
public class RegisterRequest {
    @NotBlank(message = "İstifadəçi adı boş ola bilməz")
    @Size(min = 3, max = 50, message = "İstifadəçi adı 3-50 simvol arasında olmalıdır")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "İstifadəçi adı yalnız hərf, rəqəm və alt xətt içərə bilər")
    @Schema(description = "Unikal istifadəçi adı", example = "john_doe", required = true)
    private String username;

    @NotBlank(message = "Parol boş ola bilməz")
    @Size(min = 8, max = 100, message = "Parol ən azı 8 simvol olmalıdır")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
            message = "Parol ən azı bir kiçik hərf, bir böyük hərf və bir rəqəm içərməlidir")
    @Schema(description = "Güclü parol", example = "MyPassword123", required = true)
    private String password;

    @NotBlank(message = "Email boş ola bilməz")
    @Email(message = "Düzgün email formatı daxil edin")
    @Size(max = 100, message = "Email 100 simvoldan çox ola bilməz")
    @Schema(description = "Email ünvanı", example = "john@example.com", required = true)
    private String email;

    @Pattern(regexp = "^(CUSTOMER|STAFF|ADMIN)$", message = "Rol yalnız CUSTOMER, STAFF və ya ADMIN ola bilər")
    @Schema(description = "İstifadəçi rolu", example = "CUSTOMER", defaultValue = "CUSTOMER")
    private String role = "CUSTOMER";

    @Schema(description = "Parolu təsdiqləmək", example = "MyPassword123")
    private String confirmPassword;

    @AssertTrue(message = "Parollar uyğun gəlmir")
    public boolean isPasswordsMatch() {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }
}

