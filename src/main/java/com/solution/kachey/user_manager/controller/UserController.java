package com.solution.kachey.user_manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.solution.kachey.user_manager.model.User;
import com.solution.kachey.user_manager.repo.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/profile")
	public User userProfile(@AuthenticationPrincipal UserDetails userDetails) {
		// Extract the username from the authenticated principal (from JWT)
        String username = userDetails.getUsername();
        
        // Fetch the user details from the database using the username
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
	}

}
