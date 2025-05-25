package org.userservice.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@Profile("!kafka")    // only active when you are *not* using the 'kafka' profile
@RequiredArgsConstructor
public class NoOpKafkaPublisher implements IKafkaPublisher {

    @Override
    public void publishAuthUserCreated(String email, String rawPassword) {
        // no‐op
    }

    @Override
    public void publishEmailVerification(String email, String token) {
        // no‐op
    }
}
