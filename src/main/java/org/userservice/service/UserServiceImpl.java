package org.userservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.userservice.dto.*;
import org.userservice.mapper.UserMapper;
import org.userservice.model.Role;
import org.userservice.model.User;
import org.userservice.repository.RoleRepository;
import org.userservice.repository.UserRepository;
import org.userservice.repository.UserRoleRepository;
import org.userservice.security.CustomUserDetails;
import org.userservice.security.IJwtTokenProvider;

import java.util.*;


import java.time.Instant;
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
    private final AuthenticationManager authenticationManager;
    private final UserRoleRepository userRoleRepository;

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
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEnabled(false);

        userRepository.save(user);

        // Вот тут вызов:
        verificationService.createVerificationToken(user);

        return userMapper.toDto(user);
    }@Override
    public AuthTokenDto login(UserLoginDto dto) {
        // 1. Аутентифицируем пользователя
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Получаем UserDetails (Spring Security)
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 3. Получаем User из репозитория (чтобы получить ID)
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 4. Получаем роли как список строк
        List<String> roleNames = userRoleRepository.findRoleNamesByUserId(user.getId());

        // 5. Генерируем JWT-токен (используем userDetails и список ролей)
        String token = jwtTokenProvider.generateToken(userDetails, roleNames);

        // 6. Достаем время истечения токена
        Instant expiry = jwtTokenProvider.getExpiryFromToken(token);

        // 7. Возвращаем DTO с токеном, временем жизни и ролями
        return new AuthTokenDto(token, expiry, roleNames);
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
