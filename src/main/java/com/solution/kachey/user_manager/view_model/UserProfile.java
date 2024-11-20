package com.solution.kachey.user_manager.view_model;

import com.solution.kachey.user_manager.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@Data
public class UserProfile {
    private String id; // Use String for MongoDB's ObjectId

    @NotEmpty(message = "Username is required")
    @Indexed(unique = true) // Creates a unique index on this field in MongoDB
    private String username;

    @NotEmpty(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private List<@Pattern(
            regexp = "^\\+880(17|15|19|13|16|18|14)\\d{8,9}$",
            message = "Invalid phone number format"
    ) String> phoneNumbers;

    @Indexed(unique = true) // Creates a unique index on this field in MongoDB
    private List<@Email(message = "Invalid email format") String> emails;

    private String roleName;
}
