package com.solution.kachey.auth.view_model;

import com.solution.kachey.user_manager.model.Role;
import com.solution.kachey.user_manager.model.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class RegisterRequest {
    // Getters and Setters
    private User user;
    private Role role;

}
