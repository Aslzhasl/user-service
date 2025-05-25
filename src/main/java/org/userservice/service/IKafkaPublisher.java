package org.userservice.service;

public interface IKafkaPublisher {
    void publishAuthUserCreated(String email, String rawPassword);
    void publishEmailVerification(String email, String token);
}
