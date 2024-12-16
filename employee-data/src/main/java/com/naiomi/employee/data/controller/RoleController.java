package com.naiomi.employee.data.controller;

import com.naiomi.employee.data.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing roles.
 * Provides an endpoint to delete roles and handle associated business logic.
 */
@RestController
@RequestMapping("/roles")
@Tag(name = "Role Controller", description = "Endpoints for managing roles")
public class RoleController {

    private final RoleService roleService;

    /**
     * Constructor for RoleController.
     *
     * @param roleService the service for managing role-related operations
     */
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Deletes a role and performs associated operations.
     * Deletes all employees associated with the specified role and reassigns their projects
     * to a default employee.
     *
     * @param roleId            the ID of the role to delete
     * @param defaultEmployeeId the ID of the default employee to reassign projects to
     * @return a success message indicating the role deletion and project reassignment
     */
    @DeleteMapping("/{roleId}")
    @Operation(summary = "Delete a role", description = "Deletes a role and all employees associated with it. Reassigns projects to a default employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role deleted successfully and projects reassigned."),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    public ResponseEntity<String> deleteRole(
            @Parameter(description = "ID of the role to delete", required = true) @PathVariable Long roleId,
            @Parameter(description = "ID of the default employee to reassign projects to", required = true) @RequestParam Long defaultEmployeeId) {
        roleService.deleteRole(roleId, defaultEmployeeId);
        return ResponseEntity.ok("Role and associated employees deleted. Projects reassigned.");
    }
}
