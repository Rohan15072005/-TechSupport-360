package com.helpdesk.auth.controller;

import com.helpdesk.auth.dto.AuthResponse;
import com.helpdesk.auth.dto.LoginRequest;
import com.helpdesk.auth.dto.RegisterRequest;
import com.helpdesk.auth.service.AuthService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(
            @Valid @RequestBody RegisterRequest request) {

        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request) {

        return authService.login(request);
    }
}