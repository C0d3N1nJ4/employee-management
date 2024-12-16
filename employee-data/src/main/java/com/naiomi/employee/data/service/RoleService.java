package com.naiomi.employee.data.service;

import com.naiomi.employee.data.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void deleteRole(Long roleToDelete, Long defaultEmployeeId) {
        roleRepository.deleteRoleWithEmployeesAndReassignProjects(roleToDelete, defaultEmployeeId);
    }
}

