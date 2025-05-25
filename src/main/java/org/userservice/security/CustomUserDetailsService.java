package org.userservice.security;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.userservice.model.User;
import org.userservice.repository.UserRepository;


import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String subject) throws UsernameNotFoundException {
        // attempt to parse the subject as a UUID:
        User user = null;
        try {
            UUID userId = UUID.fromString(subject);
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found by ID: " + subject));
        } catch (IllegalArgumentException e) {
            // if it wasn't a UUID, fall back to email lookup (optional):
            user = userRepository.findByEmail(subject)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found by email: " + subject));
        }

        // build and return your Spring Security UserDetails implementation:
        return new CustomUserDetails(user);
    }
}




//return new org.springframework.security.core.userdetails.User(
//        user.getEmail(),
//                user.getPassword(),
//authorities
//        );