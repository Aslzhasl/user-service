package org.userservice.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.List;

public interface IJwtTokenProvider {
    String generateToken(UserDetails userDetails, List<String> roles);
    boolean validateToken(String token);
    String getUsernameFromToken(String token);
    Instant getExpiryFromToken(String token);
}
