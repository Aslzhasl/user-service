package org.userservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.userservice.dto.*;
import org.userservice.mapper.UserMapper;
import org.userservice.model.Role;
import org.userservice.model.User;
import org.userservice.repository.RoleRepository;
import org.userservice.repository.UserRepository;
import org.userservice.security.IJwtTokenProvider;

import java.util.HashSet;
import java.util.Set;


import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final IKafkaPublisher kafkaPublisher;
    private final IJwtTokenProvider jwtTokenProvider;// assume you defined this interface
    private final IVerificationService verificationService;

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateByEmail(String email, UserRegistrationDto updates) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

        // apply allowed updates
        user.setFirstName(updates.getFirstName());
        user.setLastName(updates.getLastName());
        // if you want to allow email change you can uncomment:
        // user.setEmail(updates.getEmail());

        User saved = userRepository.save(user);

        verificationService.createVerificationToken(saved);
        return userMapper.toDto(saved);
    }
    @Override
    public UserResponseDto register(UserRegistrationDto dto) {
        // 1. Map to entity
        User user = userMapper.toEntity(dto);

        // 2. Hash password
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // 3. Assign default role
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Default role not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        // 4. Persist
        user = userRepository.save(user);

        // 5. Publish event (email + raw password, or token reset link)
        kafkaPublisher.publishAuthUserCreated(user.getEmail(), dto.getPassword());

        // 6. Return DTO
         User saved = userRepository.save(user);
        verificationService.createVerificationToken(saved);
        return userMapper.toDto(user);
    }

    @Override
    public AuthTokenDto login(UserLoginDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Map Set<Role> → Set<String>
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        // Now this matches IJwtTokenProvider.generateToken(...)
         String token = jwtTokenProvider.generateToken(user.getId().toString(), roleNames);
        Instant expiry = jwtTokenProvider.getExpiryFromToken(token);

        return new AuthTokenDto(token, expiry);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto update(UUID userId, UserRegistrationDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        // optionally change email/password with validations...
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> listAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}
