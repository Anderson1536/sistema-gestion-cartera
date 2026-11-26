package com.cartera.backend.controller;

import com.cartera.backend.dto.request.LoginRequest;
import com.cartera.backend.dto.response.LoginResponse;
import com.cartera.backend.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {

        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> iniciarSesion(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.iniciarSesion(request)
        );
    }
}
