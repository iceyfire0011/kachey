package com.solution.kachey.user_manager.service;

import java.util.Optional;
import java.util.Set;

import com.solution.kachey.user_manager.exception.InvalidRoleException;
import com.solution.kachey.user_manager.model.Permission;
import com.solution.kachey.user_manager.repo.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.solution.kachey.user_manager.Constants;
import com.solution.kachey.user_manager.model.Role;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    public Role saveOrUpdate(Role role) {
        return roleRepository.save(role);
    }

    // Helper method to validate role
    public boolean isValidRole(Role role) {
        return Constants.ROLE_SUPER_ADMIN.equals(role.getRoleName()) || Constants.ROLE_ADMIN.equals(role.getRoleName())
                || Constants.ROLE_MANAGER.equals(role.getRoleName()) || Constants.ROLE_SALES.equals(role.getRoleName())
                || Constants.ROLE_DELIVERY.equals(role.getRoleName()) || Constants.ROLE_SUPPLIER.equals(role.getRoleName())
                || Constants.ROLE_CUSTOMER.equals(role.getRoleName()) || Constants.ROLE_DEALER.equals(role.getRoleName());
    }

    public Role addRole(Role role) {
        Optional<Role> existingRole = roleRepository.findByRoleName(role.getRoleName());
        Role newRole;
        if (existingRole.isPresent()) {
            newRole = existingRole.get();
        } else {
            newRole = new Role();
            newRole.setRoleName(role.getRoleName());
            newRole = roleRepository.save(newRole);
            if (newRole.getId().isEmpty()) {
                throw new InvalidRoleException("Role has not created");
            }
        }
        return newRole;
    }

    public Role addRoleByRoleName(String roleName) {
        Optional<Role> existingRole = roleRepository.findByRoleName(roleName);
        Role newRole;
        if (existingRole.isPresent()) {
            newRole = existingRole.get();
        } else {
            newRole = new Role();
            newRole.setRoleName(roleName);
            newRole = roleRepository.save(newRole);
            if (newRole.getId().isEmpty()) {
                throw new InvalidRoleException("Role has not created");
            }
        }
        return newRole;
    }

    public Optional<Role> findByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

    public void defineRolePermissions(Role role, Set<Permission> permissions) {

    }


}
