package com.solution.kachey.user_manager.service;

import com.solution.kachey.user_manager.model.Permission;
import com.solution.kachey.user_manager.repo.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
