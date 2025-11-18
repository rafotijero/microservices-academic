package com.proyecto.auth.controller;

import com.proyecto.auth.dto.*;
import com.proyecto.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        ApiResponse response = authService.register(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout() {
        // En una implementación real, aquí se invalidaría el token en una blacklist
        // Por ahora, simplemente retornamos un mensaje exitoso
        return ResponseEntity.ok(new ApiResponse(true, "Logout exitoso"));
    }

    @GetMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @RequestParam(name = "token", required = false) String token) {

        // Extraer token del header si está presente
        String tokenToValidate = token;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            tokenToValidate = authHeader.substring(7);
        }

        TokenValidationResponse response = authService.validateToken(tokenToValidate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        UserResponse response = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(response);
    }
}
