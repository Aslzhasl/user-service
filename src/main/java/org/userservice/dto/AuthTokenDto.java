package org.userservice.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class AuthTokenDto {
    private String token;
    private Instant expiresAt;
    private String tokenType = "Bearer";

    public AuthTokenDto(String token, Instant expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }
}
