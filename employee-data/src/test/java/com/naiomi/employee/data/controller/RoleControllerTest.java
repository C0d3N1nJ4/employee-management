package com.naiomi.employee.data.controller;

import com.naiomi.employee.data.service.RoleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RoleControllerTest {

    @Test
    @DisplayName("Should delete role and return success message")
    void deleteRole_ShouldInvokeServiceAndReturnSuccessMessage() {
        // Arrange
        RoleService mockRoleService = Mockito.mock(RoleService.class);
        RoleController roleController = new RoleController(mockRoleService);

        Long roleId = 1L;
        Long defaultEmployeeId = 2L;

        // Act
        ResponseEntity<String> response = roleController.deleteRole(roleId, defaultEmployeeId);

        // Assert
        verify(mockRoleService, times(1)).deleteRole(roleId, defaultEmployeeId);
        assertEquals(ResponseEntity.ok("Role and associated employees deleted. Projects reassigned."), response);
    }
}
