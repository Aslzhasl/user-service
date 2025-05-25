package org.userservice.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailVerificationEvent {
    private String email;
    private String token;
}