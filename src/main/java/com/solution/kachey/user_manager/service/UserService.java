package com.solution.kachey.user_manager.service;

import com.solution.kachey.user_manager.model.Permission;
import com.solution.kachey.user_manager.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.solution.kachey.user_manager.model.User;
import com.solution.kachey.user_manager.repo.UserRepository;
//01711337079
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerUser(User user) {

        // Ensure at least one unique email or phone number
        if ((user.getEmails() == null || user.getEmails().isEmpty()) &&
                (user.getPhoneNumbers() == null || user.getPhoneNumbers().isEmpty())) {
            throw new IllegalArgumentException("At least one email or phone number is required.");
        }

        // Check for unique email
        if (user.getEmails() != null) {
            user.getEmails().forEach(email -> {
                if (userRepository.findByEmails(email).isPresent()) {
                    throw new IllegalArgumentException("Email already exists: " + email);
                }
            });
        }

        // Check for unique phone number
        if (user.getPhoneNumbers() != null) {
            user.getPhoneNumbers().forEach(phone -> {
                if (userRepository.findByPhoneNumbers(phone).isPresent()) {
                    throw new IllegalArgumentException("Phone number already exists: " + phone);
                }
            });
        }

        // Hash password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(
                user.get().getUsername(),
                user.get().getPassword(),
                new ArrayList<>() // Replace with proper roles/authorities
        );
    }

    // Combine role-based and user-specific permissions
    public Set<Permission> addEffectivePermissions(User user, Set<Permission> permissions) {
        if (!permissions.isEmpty()) {
            user.setPermissions(permissions);
            return user.getPermissions();
        }
        if (user.getPermissions().isEmpty()) {
            Role role = user.getRole();
            user.setPermissions(role.getPermissions());
            return user.getPermissions();
        }
        return user.getPermissions();
    }
}
