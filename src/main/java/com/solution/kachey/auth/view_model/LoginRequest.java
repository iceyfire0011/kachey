package com.solution.kachey.auth.view_model;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String email;
    private String phoneNumber;
    private String password;
}
