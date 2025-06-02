// src/main/java/org/userservice/repository/UserRepository.java
package org.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.userservice.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
