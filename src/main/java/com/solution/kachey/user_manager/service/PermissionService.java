package com.solution.kachey.user_manager.service;

import com.solution.kachey.user_manager.model.Permission;
import com.solution.kachey.user_manager.repo.PermissionRepository;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public boolean isPermissionExist(Permission permission) {
        return permissionRepository.findByApiMethodAndApiUrl(permission.getApiMethod(), permission.getApiUrl()) != null;
    }

    public Permission savePermission(Permission permission) {

        return permissionRepository.save(permission);
    }

    public List<Permission> allPermissionList() {
        return permissionRepository.findAll();
    }

    public Permission findByPermissionName(@NotEmpty(message = "Permission name is required") String permissionName) {
        return permissionRepository.findByPermissionName(permissionName);
    }

    public void updatePermission(Permission permission) {
        // Ensure the permission exists
        if (permission.getID() == null) {
            throw new IllegalArgumentException("Permission ID cannot be null for update.");
        }

        // Save the updated permission back to the database
        permissionRepository.save(permission);
    }

    public void deletePermission(Permission existingPermission) {
        permissionRepository.delete(existingPermission);
    }

    public List<Permission> setupPermissions() {
        return List.of(
                new Permission("permission-list", "/api/permission/list", "GET", true, ""),
                new Permission("edit-permission", "/api/permission/edit-permission", "GET", false, ""),
                new Permission("edit-permission-submit", "/api/permission/edit-permission", "PATCH", false, ""),
                new Permission("edit-permission-replace", "/api/permission/edit-permission", "PUT", false, ""),
                new Permission("view-profile", "/api/user/profile", "GET", false, ""),
                new Permission("edit-profile", "/api/user/edit-profile", "GET", false, ""),
                new Permission("update-profile", "/api/user/update-profile", "GET", false, "")
        );
    }

}
