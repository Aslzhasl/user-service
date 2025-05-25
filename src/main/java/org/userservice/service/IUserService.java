package org.userservice.service;

import org.userservice.dto.AuthTokenDto;
import org.userservice.dto.UserRegistrationDto;
import org.userservice.dto.UserResponseDto;
import org.userservice.dto.UserLoginDto;

import java.util.List;
import java.util.UUID;
public interface IUserService {
    // existing methods…
    UserResponseDto register(UserRegistrationDto registration);
    AuthTokenDto login(UserLoginDto login);
    UserResponseDto getById(UUID userId);
    UserResponseDto update(UUID userId, UserRegistrationDto updates);
    List<UserResponseDto> listAll();

    // new methods for “me” endpoint
    UserResponseDto getByEmail(String email);
    UserResponseDto updateByEmail(String email, UserRegistrationDto updates);
}

