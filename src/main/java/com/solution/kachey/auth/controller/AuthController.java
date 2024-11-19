package com.solution.kachey.auth.controller;

import com.solution.kachey.auth.model.JwtResponse;
import com.solution.kachey.auth.view_model.RegisterRequest;
import com.solution.kachey.config.exception.GlobalExceptionHandler;
import com.solution.kachey.user_manager.Constants;
import com.solution.kachey.user_manager.exception.InvalidRoleException;
import com.solution.kachey.user_manager.model.Permission;
import com.solution.kachey.user_manager.model.Role;
import com.solution.kachey.user_manager.service.PermissionService;
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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

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
            return ResponseEntity.ok(new JwtResponse(token, user.getUsername(),
                    user.getPermissions().stream()
                            .map(Permission::getPermissionName) // Extract the name of each Permission
                            .distinct() // Ensure no duplicate names
                            .collect(Collectors.joining(", "))));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/setup")
    public String setup() {
        List<Permission> permissions = permissionService.setupPermission();
        Role newRole = roleService.addRoleByRoleName(Constants.ROLE_SUPER_ADMIN);
        newRole.setPermissions(permissions);
        roleService.saveOrUpdate(newRole);
        Optional<User> user = userService.findByUsername("super_admin");
        User newUser;
        if (user.isPresent()) {
            newUser = user.get();
        } else {
            newUser = new User();
            newUser.setUsername("super_admin");
            newUser.setPassword("1234");
            newUser.setEmails(List.of("super_admin@kachey.com"));
        }
        newUser.setPermissions(permissions);
        newUser.setRole(newRole);
        userService.saveOrUpdate(newUser);
        return "User " + newUser.getUsername() + " has registered successfully!";
    }
}
