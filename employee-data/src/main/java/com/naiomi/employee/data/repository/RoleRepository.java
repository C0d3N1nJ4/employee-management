package com.naiomi.employee.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.naiomi.employee.data.model.Role;

/**
 * Repository interface for managing Role entities.
 * <p>
 * Extends {@link JpaRepository} to provide standard CRUD operations and
 * integrates custom database procedures.
 * </p>
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Executes the stored procedure `delete_role_with_employees_and_reassign_projects` to delete a role,
     * remove employees associated with that role, and reassign their projects to a default employee.
     *
     * @param roleToDelete       The ID of the role to delete.
     * @param defaultEmployeeId  The ID of the employee to whom projects should be reassigned.
     */
    @Procedure(procedureName = "delete_role_with_employees_and_reassign_projects")
    void deleteRoleWithEmployeesAndReassignProjects(@Param("role_to_delete") Long roleToDelete,
                                                    @Param("default_employee_id") Long defaultEmployeeId);
}
