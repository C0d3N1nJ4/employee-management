package com.naiomi.employee.data.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

class StoredProcedureLogicTest {

    @Test
    @DisplayName("Test deletion of employees and reassignment of projects during role deletion")
    void testDeleteRoleWithEmployeesAndReassignProjects() throws SQLException {
        // Arrange
        Long roleId = 1L;
        Long defaultEmployeeId = 2L;

        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockDeleteEmployeesStmt = mock(PreparedStatement.class);
        PreparedStatement mockUpdateProjectsStmt = mock(PreparedStatement.class);

        // Mock the behavior of the connection and prepared statements
        when(mockConnection.prepareStatement("DELETE FROM employees WHERE role_id = ?"))
                .thenReturn(mockDeleteEmployeesStmt);
        when(mockConnection.prepareStatement(
                "UPDATE projects SET employee_id = ? WHERE employee_id IN (SELECT id FROM employees WHERE role_id = ?)"))
                .thenReturn(mockUpdateProjectsStmt);

        // Act
        StoredProcedureLogic.deleteRoleWithEmployeesAndReassignProjects(mockConnection, roleId, defaultEmployeeId);

        // Assert
        // Verify the first PreparedStatement execution
        verify(mockDeleteEmployeesStmt).setLong(1, roleId);
        verify(mockDeleteEmployeesStmt).executeUpdate();

        // Verify the second PreparedStatement execution
        verify(mockUpdateProjectsStmt).setLong(1, defaultEmployeeId);
        verify(mockUpdateProjectsStmt).setLong(2, roleId);
        verify(mockUpdateProjectsStmt).executeUpdate();

        // Verify the prepared statements are closed
        verify(mockDeleteEmployeesStmt).close();
        verify(mockUpdateProjectsStmt).close();

        // Verify the connection isn't closed in this method (it's handled outside)
        verify(mockConnection, never()).close();
    }
}
