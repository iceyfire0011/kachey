package com.solution.kachey.auth.controller;

import com.solution.kachey.auth.model.JwtResponse;
import com.solution.kachey.auth.view_model.LoginRequest;
import com.solution.kachey.auth.view_model.RegisterRequest;
import com.solution.kachey.config.default_model.UserConfig;
import com.solution.kachey.user_manager.Constants;
import com.solution.kachey.user_manager.exception.InvalidRoleException;
import com.solution.kachey.user_manager.model.Permission;
import com.solution.kachey.user_manager.model.Role;
import com.solution.kachey.user_manager.service.PermissionService;
import com.solution.kachey.user_manager.service.RoleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.solution.kachey.config.jwt.JwtTokenUtil;
import com.solution.kachey.user_manager.model.User;
import com.solution.kachey.user_manager.service.UserService;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserConfig userConfig;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public AuthController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        if (userService.existsByUsername(request.getUser().getUsername())) {
            return "Username already exists!";
        }

        // Validate and assign role
        if (!roleService.isValidRole(request.getRole())) {
            throw new InvalidRoleException("Invalid role: " + request.getRole().getRoleName());
        }
        Role newRole = roleService.saveRole(request.getRole());
        request.getUser().setRole(newRole);

        // Register user
        User newUser = userService.registerUser(request.getUser());
        return "User " + newUser.getUsername() + " has registered successfully!";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Find the user by username, email, or phone number
        Optional<User> optionalUser = userService.findByUsernameOrEmailOrPhoneNumber(
                request.getUsername(), request.getEmail(), request.getPhoneNumber()
        );

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User does not exist!");
        }

        User loggedInUser = optionalUser.get();

        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loggedInUser.getUsername(), request.getPassword())
        );

        // Generate JWT token
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(
                token,
                loggedInUser.getUsername(),
                loggedInUser.getPermissions(),
                loggedInUser.getRole()
        ));
    }

    @PostMapping("/setup")
    public String setup() {
        ModelMapper modelMapper = new ModelMapper();

        // Step 1: Retrieve setup permissions
        List<Permission> setupPermissions = permissionService.setupPermissions();

        // Step 2: Find all existing permissions in the database
        List<Permission> existingPermissions = permissionService.allPermissionList();
        List<String> setupPermissionNames = setupPermissions.stream()
                .map(Permission::getPermissionName)
                .toList();

        // Step 3: Delete missing permissions
        for (Permission existingPermission : existingPermissions) {
            if (!setupPermissionNames.contains(existingPermission.getPermissionName())) {
                // Remove permission from users and roles
                List<User> usersWithPermission = userService.findByPermission(existingPermission);
                for (User user : usersWithPermission) {
                    user.getPermissions().remove(existingPermission);
                    userService.saveUser(user); // Save after removal
                }

                List<Role> rolesWithPermission = roleService.findByPermission(existingPermission);
                for (Role role : rolesWithPermission) {
                    role.getPermissions().remove(existingPermission);
                    roleService.saveRole(role); // Save after removal
                }

                // Delete permission from the permission collection
                permissionService.deletePermission(existingPermission);
            }
        }

        // Step 4: Iterate over setupPermissions and update or add them
        for (Permission setupPermission : setupPermissions) {
            Permission existingPermission = permissionService.findByPermissionName(setupPermission.getPermissionName());
            if (existingPermission != null) {
                // Update the existing permission with ModelMapper
                String existingId = existingPermission.getID(); // Preserve ID
                modelMapper.map(setupPermission, existingPermission);
                existingPermission.setID(existingId); // Ensure ID is not lost
                permissionService.updatePermission(existingPermission);
            } else {
                // Add new permission
                permissionService.savePermission(setupPermission);
            }
        }

        // Step 5: Retrieve all permissions after updates
        List<Permission> allPermissions = permissionService.allPermissionList();

        // Step 6: Create or update SUPER_ADMIN role
        Role superAdminRole = roleService.findByRoleName(Constants.ROLE_SUPER_ADMIN)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setRoleName(Constants.ROLE_SUPER_ADMIN);
                    return roleService.saveRole(newRole);
                });

        // Assign all permissions to SUPER_ADMIN role
        superAdminRole.setPermissions(allPermissions);
        roleService.saveRole(superAdminRole);

        // Step 7: Create or update admin user
        User adminUser = userService.findByUsername("super_admin")
                .orElseGet(() -> {
                    User newUser = new User();
                    return userService.saveUser(newUser);
                });
        // Assign SUPER_ADMIN role and permissions to admin user
        String adminUserId =adminUser.getId();
        modelMapper.map(userConfig,adminUser);
        adminUser.setId(adminUserId);
        adminUser.setPassword(passwordEncoder.encode(userConfig.getPassword()));
        adminUser.setRole(superAdminRole);
        adminUser.setPermissions(allPermissions);
        userService.saveUser(adminUser);

        return "Setup completed successfully! Admin user and SUPER_ADMIN role configured.";
    }

}
