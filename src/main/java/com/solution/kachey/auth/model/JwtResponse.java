package com.solution.kachey.auth.model;

import com.solution.kachey.user_manager.model.Permission;
import com.solution.kachey.user_manager.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String username;
    private List<Permission> permissions;
    private Role role;

    // Getters and Setters
}
