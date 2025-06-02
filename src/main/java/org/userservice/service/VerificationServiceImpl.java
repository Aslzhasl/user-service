package org.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.userservice.model.EmailVerificationToken;
import org.userservice.model.User;
import org.userservice.repository.EmailVerificationTokenRepository;
import org.userservice.repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements IVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Override
    public void createVerificationToken(User user) {
        // 1. Генерируем токен
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken(
                null, token, user, Instant.now().plus(1, ChronoUnit.DAYS)
        );
        tokenRepository.save(verificationToken);

        // 2. Ссылка для активации
        String link = "http://localhost:8080/api/users/verify?token=" + token;

        // 3. Отправляем email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Email Verification");
        message.setText("Please verify your email by clicking the link: " + link);
        mailSender.send(message);
    }

    @Override
    public void verifyToken(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found!"));

        if (verificationToken.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Token expired!");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        tokenRepository.delete(verificationToken);
    }
}
