package org.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.userservice.model.EmailVerificationToken;
import org.userservice.model.User;
import org.userservice.repository.EmailVerificationTokenRepository;
import org.userservice.repository.UserRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class VerificationServiceImpl implements IVerificationService {

    private final EmailVerificationTokenRepository tokenRepo;
    private final UserRepository userRepo;
    private final IKafkaPublisher kafkaPublisher;
    private final EmailService emailService;

    /** tokens valid for 24h */
    private final Duration validity = Duration.ofHours(24);

    @Override
    public void createVerificationToken(User user) {
        tokenRepo.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        Instant expires = Instant.now().plus(validity);

        EmailVerificationToken evt = new EmailVerificationToken();
        evt.setToken(token);
        evt.setUser(user);
        evt.setExpiresAt(expires);
        tokenRepo.save(evt);

        // still a no-op in no-kafka mode:
        //kafkaPublisher.publishEmailVerification(user.getEmail(), token);

        // ALWAYS send the actual e-mail:
        String link = String.format(
                "http://localhost:8080/api/users/verify?token=%s", token
        );
       // emailService.sendVerificationEmail(user.getEmail(), link);
    }

    @Override
    public void verifyToken(String token) {
        EmailVerificationToken evt = tokenRepo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));

        if (evt.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalStateException("Verification token expired");
        }

        // enable the user and clean up
        User u = evt.getUser();
        u.setEnabled(true);
        userRepo.save(u);
        tokenRepo.delete(evt);
    }
}
