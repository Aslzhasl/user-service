package org.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.userservice.dto.*;
import org.userservice.service.IUserService;

import jakarta.validation.Valid;
import org.userservice.service.IVerificationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    IVerificationService verificationService;


    /** 1️⃣ Register **/
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(
            @Valid @RequestBody UserRegistrationDto dto) {
        var created = userService.register(dto);
        return ResponseEntity.ok(created);
    }

    /** 2️⃣ Login **/
    @PostMapping("/login")
    public ResponseEntity<AuthTokenDto> login(
            @Valid @RequestBody UserLoginDto dto) {
        var token = userService.login(dto);
        return ResponseEntity.ok(token);
    }

    /** 3️⃣ Get current user (“me”) **/
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me(Authentication auth) {
        return ResponseEntity.ok(userService.getByEmail(auth.getName()));
    }

    /** 4️⃣ Update current user **/
    @PutMapping("/me")
    public ResponseEntity<UserResponseDto> updateMe(
            Authentication auth,
            @Valid @RequestBody UserRegistrationDto updates) {
        return ResponseEntity.ok(userService.updateByEmail(auth.getName(), updates));
    }

    /** 5️⃣ List all users (ADMIN only) **/
    @GetMapping
    public List<UserResponseDto> listAll() {
        return userService.listAll();
    }


    /** 6️⃣ Get any user by ID (ADMIN only) **/
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getById(id));
    }
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        verificationService.verifyToken(token);
        return ResponseEntity.ok("Email verified successfully");
    }
}
