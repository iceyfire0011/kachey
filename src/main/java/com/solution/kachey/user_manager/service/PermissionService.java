package com.solution.kachey.user_manager.service;

import com.solution.kachey.user_manager.model.Permission;
import com.solution.kachey.user_manager.repo.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
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

    public List<Permission> setupPermission() {
        List<Permission> permissions = List.of(
                new Permission("permission-list", "/api/permission/list", "GET", true, ""),
                new Permission("add-permission", "/api/permission/add-permission", "GET", true, ""),
                new Permission("add-permission-submit", "/api/permission/add-permission", "POST", false, ""),
                new Permission("edit-permission", "/api/permission/edit-permission", "GET", false, ""),
                new Permission("edit-permission-submit", "/api/permission/edit-permission", "PATCH", false, ""),
                new Permission("edit-permission-replace", "/api/permission/edit-permission", "PUT", false, "")
        );
        for (Permission permission : permissions) {
            // Check if the permission already exists in the database
            boolean exists = permissionRepository.existsByPermissionNameAndApiMethodAndApiUrl(
                    permission.getPermissionName(),
                    permission.getApiUrl(),
                    permission.getApiMethod()
            );

            if (!exists) {
                permissionRepository.save(permission); // Add permission if it doesn't exist
            }
        }
        return allPermissionList();
    }
}
