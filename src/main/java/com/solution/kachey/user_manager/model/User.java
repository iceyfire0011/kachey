package com.solution.kachey.user_manager.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.solution.kachey.config.validation.UniquePhoneNumbers;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Document(collection = "users") // Specifies the collection name in MongoDB
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id // Marks this field as the primary key
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

    private Role role;

    @DBRef
    private Set<Permission> permissions;
}
