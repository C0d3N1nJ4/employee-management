package com.naiomi.employee.data.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StoredProcedureLogic {

    public static void deleteRoleWithEmployeesAndReassignProjects(Connection conn, Long roleId, Long defaultEmployeeId) throws SQLException {
        try (PreparedStatement deleteEmployees = conn.prepareStatement(
                "DELETE FROM employees WHERE role_id = ?");
             PreparedStatement updateProjects = conn.prepareStatement(
                     "UPDATE projects SET employee_id = ? WHERE employee_id IN (SELECT id FROM employees WHERE role_id = ?)")) {

            // Delete employees with the given role ID
            deleteEmployees.setLong(1, roleId);
            deleteEmployees.executeUpdate();

            // Reassign projects to the default employee ID
            updateProjects.setLong(1, defaultEmployeeId);
            updateProjects.setLong(2, roleId);
            updateProjects.executeUpdate();
        }
    }
}
