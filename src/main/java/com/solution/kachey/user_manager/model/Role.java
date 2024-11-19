package com.solution.kachey.user_manager.model;

import java.util.List;

import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "roles")
public class Role {
    @Id
    private String id;
    @NotEmpty(message = "roleName is required")
    @Indexed(unique = true) // Creates a unique index on this field in MongoDB
    private String roleName;
    @DBRef(lazy = true)
    private List<Permission> permissions; // Permissions assigned to the role
}
