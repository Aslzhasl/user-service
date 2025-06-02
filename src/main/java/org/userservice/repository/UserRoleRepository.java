// src/main/java/org/userservice/repository/UserRoleRepository.java
package org.userservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.userservice.model.UserRole;

import java.util.List;
import java.util.UUID;

public interface UserRoleRepository extends CrudRepository<UserRole, Long> {
    // Возвращаем все имена ролей (String) для указанного user_id
    @Query("SELECT r.name FROM UserRole ur JOIN ur.role r WHERE ur.user.id = :userId")
    List<String> findRoleNamesByUserId(@Param("userId") UUID userId);
}
