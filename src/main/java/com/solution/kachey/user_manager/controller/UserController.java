package com.solution.kachey.user_manager.controller;

import com.solution.kachey.user_manager.view_model.UserProfile;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ModelMap;
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
	
	@GetMapping("/view-profile")
	public UserProfile viewUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
		// Extract the username from the authenticated principal (from JWT)
        String username = userDetails.getUsername();
        
		ModelMapper modelMapper = new ModelMapper();
        // Fetch the user details from the database using the username
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("User not found"));
		UserProfile userProfile = new UserProfile();
		modelMapper.map(user, userProfile);
        return userProfile;
	}

}
