package org.userservice.security;

import java.time.Instant;
import java.util.Set;

public interface IJwtTokenProvider {
    String generateToken(String subject, Set<String> roles);
    boolean validateToken(String token);
    String getUsernameFromToken(String token);
    Instant getExpiryFromToken(String token);
}
