package org.userservice.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthUserCreatedEvent {
    private String email;
    private String password;
}
