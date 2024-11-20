package com.solution.kachey.user_manager.service;

import com.solution.kachey.user_manager.model.Permission;
import com.solution.kachey.user_manager.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.solution.kachey.user_manager.model.User;
import com.solution.kachey.user_manager.repo.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public boolean existsByUsername(@NotEmpty(message = "Username is required") String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public User saveUser(User newUser) {
        return userRepository.save(newUser);
    }

    public List<User> findByPermission(Permission existingPermission) {
        return userRepository.findByPermissions(existingPermission);
    }

    public Optional<User> findByUsernameOrEmailOrPhoneNumber(@NotEmpty(message = "Username is required") String username, @Email(message = "Invalid email format") String email, @Pattern(
            regexp = "^\\+880(17|15|19|13|16|18|14)\\d{8,9}$",
            message = "Invalid phone number format"
    ) String phoneNumber) {
        if (username != null && !username.isEmpty()) {
            return userRepository.findByUsername(username);
        }

        if (email != null && !email.isEmpty()) {
            return userRepository.findByEmails(email);
        }

        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            return userRepository.findByPhoneNumbers(phoneNumber);
        }

        return Optional.empty();
    }
}
