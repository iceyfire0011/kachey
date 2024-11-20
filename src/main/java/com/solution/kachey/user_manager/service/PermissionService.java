package com.solution.kachey.user_manager.service;

import com.solution.kachey.user_manager.model.Permission;
import com.solution.kachey.user_manager.repo.PermissionRepository;
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

    public Permission addPermission(Permission permission) {

        return permissionRepository.save(permission);
    }

    public List<Permission> allPermissionList() {
        return permissionRepository.findAll();
    }

    public void deleteAllPermissions() {
        try {
            permissionRepository.deleteAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete permissions: " + e.getMessage());
        }
    }

    public List<Permission> saveAllPermissions(List<Permission> permissions) {
        return permissionRepository.saveAll(permissions);
    }

    public List<Permission> setupPermission() {
        return List.of(
                new Permission("permission-list", "/api/permission/list", "GET", true, ""),
                new Permission("edit-permission", "/api/permission/edit-permission", "GET", false, ""),
                new Permission("edit-permission-submit", "/api/permission/edit-permission", "PATCH", false, ""),
                new Permission("edit-permission-replace", "/api/permission/edit-permission", "PUT", false, "")
        );
    }
}
