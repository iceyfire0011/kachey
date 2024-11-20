package com.solution.kachey.user_manager.repo;

import com.solution.kachey.user_manager.model.Permission;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PermissionRepository extends MongoRepository<Permission, String> {
    Permission findByApiMethodAndApiUrl(String apiMethod, String apiUrl);
    boolean existsByPermissionNameAndApiMethodAndApiUrl(String name, String apiUrl, String apiMethod);

    Permission findByPermissionName(@NotEmpty(message = "Permission name is required") String permissionName);
}
