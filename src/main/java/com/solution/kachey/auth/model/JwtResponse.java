package com.solution.kachey.auth.model;

import com.solution.kachey.user_manager.model.Permission;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class JwtResponse {
    private String token;
    private String username;
    private Set<Permission> permission;
    private String roleName;

    public JwtResponse(String token, String username, Set<Permission> permission) {
        this.token = token;
        this.username = username;
        this.permission = permission;
    }

    public JwtResponse(String token, String username, String roleName) {
        this.token = token;
        this.username = username;
        this.roleName = roleName;
    }

    // Getters and Setters
}
