package org.userservice.dto;

import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class AuthTokenDto {
    private String token;
    private Instant expiresAt;
    private String tokenType = "Bearer";
    private List<String> roles;

    public AuthTokenDto(String token, Instant expiresAt, List<String> roles) {
        this.token = token;
        this.expiresAt = expiresAt;
        this.roles = roles;
    }
}


