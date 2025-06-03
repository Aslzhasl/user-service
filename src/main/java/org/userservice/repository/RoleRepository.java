// src/main/java/org/userservice/repository/RoleRepository.java
package org.userservice.repository;

import org.userservice.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(String name);
}
