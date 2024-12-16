package com.naiomi.employee.data.service;

import com.naiomi.employee.data.repository.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Test
    @DisplayName("Should call repository to delete role and reassign projects when deleteRole is invoked")
    void deleteRole_ShouldCallRepositoryWithCorrectParameters() {
        // Arrange
        RoleRepository mockRoleRepository = Mockito.mock(RoleRepository.class);
        RoleService roleService = new RoleService(mockRoleRepository);

        Long roleToDelete = 1L;
        Long defaultEmployeeId = 2L;

        // Act
        roleService.deleteRole(roleToDelete, defaultEmployeeId);

        // Assert
        verify(mockRoleRepository, times(1))
                .deleteRoleWithEmployeesAndReassignProjects(roleToDelete, defaultEmployeeId);
    }
}
