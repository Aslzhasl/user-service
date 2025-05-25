package org.userservice.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.userservice.dto.UserRegistrationDto;
import org.userservice.dto.UserResponseDto;
import org.userservice.model.Role;
import org.userservice.model.User;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToNames")
    UserResponseDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)            // we’ll assign roles in service
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(UserRegistrationDto dto);

    @Named("rolesToNames")
    default Set<String> rolesToNames(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
