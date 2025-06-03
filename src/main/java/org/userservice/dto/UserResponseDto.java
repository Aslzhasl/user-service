package org.userservice.dto;

import lombok.Data;
import org.userservice.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class UserResponseDto {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private Set<String> roles;
    private LocalDateTime createdAt;

    // Принимаем любой Collection<String> (можно и List, и Set)
    public UserResponseDto(User user, Collection<String> roles) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.createdAt = user.getCreatedAt();
        // Всегда сохраняем как Set — но не кастим, а создаём новый HashSet
        this.roles = new HashSet<>(roles);
    }

    public UserResponseDto() {
    }
}
