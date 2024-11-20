package com.solution.kachey.user_manager.repo;

import java.util.List;
import java.util.Optional;

import com.solution.kachey.user_manager.model.Permission;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.solution.kachey.user_manager.model.User;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, String> {
	Optional<User> findByUsername(String username);

	@Query("{ 'emails': ?0 }")
	Optional<User> findByEmails(String email);

	@Query("{ 'phoneNumbers': ?0 }")
	Optional<User> findByPhoneNumbers(String phoneNumber);

	List<User> findByPermissions(Permission permission);
}
