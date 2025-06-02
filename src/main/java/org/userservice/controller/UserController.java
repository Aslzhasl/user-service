package org.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.userservice.dto.*;
import org.userservice.model.User;
import org.userservice.repository.UserRepository;
import org.userservice.repository.UserRoleRepository;
import org.userservice.security.JwtTokenProvider;
import org.userservice.service.IUserService;

import jakarta.validation.Valid;
import org.userservice.service.IVerificationService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final IVerificationService verificationService;

    // Для логина
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        verificationService.verifyToken(token);
        return ResponseEntity.ok("Email verified successfully");
    }

    /** 1️⃣ Register **/
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(
            @Valid @RequestBody UserRegistrationDto dto) {
        var created = userService.register(dto);
        return ResponseEntity.ok(created);
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

    @PostMapping("/login")
    public ResponseEntity<AuthTokenDto> login(@RequestBody UserLoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        // Получаем User и роли
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<String> roleNames = userRoleRepository.findRoleNamesByUserId(user.getId());

        // Генерируем токен с ролями
        String token = jwtTokenProvider.generateToken(userDetails, roleNames);
        // Получаем expiry
        Instant expiry = jwtTokenProvider.getExpiryFromToken(token);

        // Возвращаем токен, expiry, роли
        return ResponseEntity.ok(new AuthTokenDto(token, expiry, roleNames));
    }

}
