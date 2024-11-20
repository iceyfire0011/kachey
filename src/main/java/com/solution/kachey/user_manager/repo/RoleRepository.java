package com.solution.kachey.user_manager.repo;

import com.solution.kachey.user_manager.model.Permission;
import com.solution.kachey.user_manager.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByRoleName(String roleName);

    List<Role> findByPermissions(Permission existingPermission);
}
