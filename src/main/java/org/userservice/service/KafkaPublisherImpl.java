package org.userservice.service;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.userservice.messaging.AuthUserCreatedEvent;
import org.userservice.messaging.EmailVerificationEvent;
@Service
@Profile("kafka")
@RequiredArgsConstructor
public class KafkaPublisherImpl implements IKafkaPublisher {

    // <--- switch to Object as the value type
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishAuthUserCreated(String email, String rawPassword) {
        kafkaTemplate.send("auth-user-created",
                new AuthUserCreatedEvent(email, rawPassword));
    }

    @Override
    public void publishEmailVerification(String email, String token) {
        kafkaTemplate.send("email-verification",
                new EmailVerificationEvent(email, token));
    }
}


