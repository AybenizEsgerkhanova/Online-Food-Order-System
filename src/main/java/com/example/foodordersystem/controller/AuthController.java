package com.example.foodordersystem.controller;

import com.example.foodordersystem.model.dto.request.AuthRequest;
import com.example.foodordersystem.model.dto.request.RegisterRequest;
import com.example.foodordersystem.model.dto.response.AuthResponse;
import com.example.foodordersystem.model.entity.User;
import com.example.foodordersystem.security.JwtUtil;
import com.example.foodordersystem.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.foodordersystem.model.dto.response.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Qeydiyyat və giriş endpoint-ləri")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager,
                          UserServiceImpl userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    @Operation(
            summary = "Yeni müştəri qeydiyyatı",
            description = "Yeni istifadəçi hesabı yaradır və JWT token qaytarır"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Qeydiyyat uğurlu",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Yanlış məlumatlar",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Username və ya email artıq mövcuddur")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody
                                                     @Parameter(description = "Qeydiyyat məlumatları", required = true)
                                                     RegisterRequest request) {
        User user = userService.registerUser(request);

        UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(),
                user.getRole().name()));
    }

    @PostMapping("/login")
    @Operation(summary = "İstifadəçi girişi", description = "Mövcud hesabla giriş edir və JWT token qaytarır")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Giriş uğurlu"),
            @ApiResponse(responseCode = "401", description = "Yanlış username və ya password"),
            @ApiResponse(responseCode = "404", description = "İstifadəçi tapılmadı")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody
                                                  @Parameter(description = "Giriş məlumatları", required = true)
                                                  AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = userService.loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(token, userDetails.getUsername(),
                userDetails.getAuthorities().iterator().next().getAuthority()));
    }
}