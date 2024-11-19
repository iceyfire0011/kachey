package com.solution.kachey.user_manager.controller;

import com.solution.kachey.user_manager.model.Permission;
import com.solution.kachey.user_manager.model.User;
import com.solution.kachey.user_manager.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/permission")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @PostMapping("/add-permission")
    public String addPermission(@RequestBody Permission permission) {
        if (permissionService.isPermissionExist(permission)) {
            return "Permission already exists!";
        }
        Permission newPermission = permissionService.addPermission(permission);
        return "New Permission \"" + newPermission.getPermissionName() + "\" has added successfully!";
    }
}
