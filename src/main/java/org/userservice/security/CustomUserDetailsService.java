package org.userservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.userservice.model.User;
import org.userservice.repository.UserRepository;
import org.userservice.repository.UserRoleRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String subject) throws UsernameNotFoundException {
        User user;
        try {
            UUID userId = UUID.fromString(subject);
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found by ID: " + subject));
        } catch (IllegalArgumentException e) {
            user = userRepository.findByEmail(subject)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found by email: " + subject));
        }

        // Получаем роли пользователя (например, ["ROLE_ADMIN", "ROLE_USER"])
        List<String> roles = userRoleRepository.findRoleNamesByUserId(user.getId());

        // Преобразуем роли в authorities Spring Security
        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Возвращаем стандартного Spring User (или свой кастомный, если нужно)
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
