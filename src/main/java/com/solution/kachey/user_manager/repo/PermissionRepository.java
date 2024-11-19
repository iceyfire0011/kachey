package com.solution.kachey.user_manager.repo;

import com.solution.kachey.user_manager.model.Permission;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PermissionRepository extends MongoRepository<Permission, String> {
    public Permission findByApiMethodAndApiUrl(String apiMethod, String apiUrl);
}
