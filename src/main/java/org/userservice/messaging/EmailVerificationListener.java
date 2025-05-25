package org.userservice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.userservice.service.EmailService;

@Slf4j
@Component
@Profile("kafka")    // ← only spin up the consumer under the 'kafka' profile
@RequiredArgsConstructor
public class EmailVerificationListener {
    private static final String VERIFY_URL =
            "http://localhost:8080/api/users/verify?token=%s";

    private final EmailService emailService;

    @KafkaListener(topics = "email-verification", groupId = "user-service")
    public void handleVerification(EmailVerificationEvent event) {
        String link = String.format(VERIFY_URL, event.getToken());
        log.info("🔗 Email verification link for {}: {}", event.getEmail(), link);
        emailService.sendVerificationEmail(event.getEmail(), link);
    }
}
