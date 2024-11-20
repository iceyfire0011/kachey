package com.solution.kachey.user_manager.repo;

import java.util.List;
import java.util.Optional;

import com.solution.kachey.user_manager.model.Permission;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.solution.kachey.user_manager.model.User;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    List<User> findByPermissions(Permission permission);

    Optional<User> findByEmails(@Email(message = "Invalid email format") String emails);

    Optional<User> findByPhoneNumbers(@Pattern(
            regexp = "^\\+880(17|15|19|13|16|18|14)\\d{8,9}$",
            message = "Invalid phone number format"
    ) String phoneNumber);
}
