package org.userservice.model;

import jakarta.persistence.*;
        import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;   // e.g. "ROLE_USER", "ROLE_ADMIN"
}
