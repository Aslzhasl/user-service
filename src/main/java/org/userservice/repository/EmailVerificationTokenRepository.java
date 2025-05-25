package org.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.userservice.model.EmailVerificationToken;
import org.userservice.model.User;

import java.util.Optional;

public interface EmailVerificationTokenRepository
        extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);
    void deleteByUser(User user);
}

