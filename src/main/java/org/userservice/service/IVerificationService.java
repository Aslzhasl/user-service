package org.userservice.service;

public interface IVerificationService {
    void createVerificationToken(org.userservice.model.User user);
    void verifyToken(String token);
}
