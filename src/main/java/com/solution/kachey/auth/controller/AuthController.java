package com.solution.kachey.auth.controller;

import com.solution.kachey.auth.model.JwtResponse;
import com.solution.kachey.auth.view_model.RegisterRequest;
import com.solution.kachey.config.exception.GlobalExceptionHandler;
import com.solution.kachey.user_manager.Constants;
import com.solution.kachey.user_manager.exception.InvalidRoleException;
import com.solution.kachey.user_manager.model.Role;
import com.solution.kachey.user_manager.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.solution.kachey.config.jwt.JwtTokenUtil;
import com.solution.kachey.user_manager.model.User;
import com.solution.kachey.user_manager.service.UserService;

import java.util.concurrent.CancellationException;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;


    public AuthController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) throws Exception {
        if (userService.findByUsername(request.getUser().getUsername()).isPresent()) {
            throw new AuthenticationServiceException("Username already exists!");
        }
        // Validate role
        if (!roleService.isValidRole(request.getRole())) {
            throw new InvalidRoleException("Invalid role: " + request.getRole().getRoleName());
        }
        Role newRole = roleService.addRole(request.getRole());
        request.setRole(newRole);
        request.getUser().setRole(request.getRole());
        User newUser = userService.registerUser(request.getUser());
        if (newUser.getId().isEmpty()) {
            throw new CancellationException();
        }
        return "User " + newUser.getUsername() + " has registered successfully!";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            if (userService.findByUsername(user.getUsername()).isEmpty()) {
                throw new UsernameNotFoundException("User does not exist!");
            }
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            user.getPassword()
                    )
            );
            String token = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());
            if (Constants.ROLE_SUPER_ADMIN.equals(user.getRole().getRoleName())) {
                return ResponseEntity.ok(new JwtResponse(token, user.getUsername(), user.getRole().getRoleName()));
            }
            return ResponseEntity.ok(new JwtResponse(token, user.getUsername(), user.getPermissions()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
