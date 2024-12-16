package com.naiomi.employee.api.controller;

import com.naiomi.employee.api.dto.EmployeeApiRequestDto;
import com.naiomi.employee.api.dto.EmployeeApiResponseDto;
import com.naiomi.employee.api.exception.EmployeeNotFoundException;
import com.naiomi.employee.api.exception.InvalidRoleException;
import com.naiomi.employee.api.model.Role;
import com.naiomi.employee.api.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/employees")
@Validated
@Tag(name = "Employee Management", description = "Endpoints for managing employees")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Creates a new employee. Only users with the ADMIN role are authorized.
     */
    @PostMapping
    @Operation(summary = "Create a new employee", description = "Creates a new employee. Only ADMIN roles are authorized.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or unauthorized role"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> createEmployee(
            @RequestHeader("Role") String roleHeader,  // <--- Retrieve role from header
            @Valid @RequestBody EmployeeApiRequestDto employeeRequest,
            BindingResult bindingResult) {

        logger.info("Received request to create employee with role header: {}", roleHeader);

        // If Bean Validation fails, return 400
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            fieldError -> fieldError.getField(),
                            fieldError -> fieldError.getDefaultMessage()
                    ));
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        // Set the role from the header into the DTO so it's not null
        employeeRequest.setRole(roleHeader);

        EmployeeApiResponseDto response = employeeService.createEmployee(employeeRequest);

        // Build response body
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("id", response.getId());
        responseBody.put("firstName", response.getFirstName());
        responseBody.put("surname", response.getSurname());
        responseBody.put("roleId", mapRoleToRoleId(response.getRole().name()));

        logger.info("Successfully created employee with ID: {}", response.getId());
        return ResponseEntity.ok(responseBody);
    }

    /**
     * Retrieves an employee's details by their ID. Allows ADMIN or USER.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get employee details by ID",
            description = "Fetch employee details by ID. USER or ADMIN role is required.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "400", description = "Invalid role")
    })
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<EmployeeApiResponseDto> getEmployeeById(@PathVariable Long id) {
        logger.info("Received request to view employee with ID: {}", id);
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    /**
     * Updates an employee's details. Only users with the USER role are authorized.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update employee details",
            description = "Updates employee details. Only USER roles are authorized.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or unauthorized role"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Map<String, Object>> updateEmployee(
            @RequestHeader("Role") String roleHeader,  // <--- Retrieve role from header
            @PathVariable Long id,
            @Valid @RequestBody EmployeeApiRequestDto employeeRequest,
            BindingResult bindingResult) {

        logger.info("Received request to update employee with ID: {} by role header: {}", id, roleHeader);

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            fieldError -> fieldError.getField(),
                            fieldError -> fieldError.getDefaultMessage()
                    ));
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        // Set the role from the header into the DTO
        employeeRequest.setRole(roleHeader);

        EmployeeApiResponseDto response = employeeService.updateEmployee(id, employeeRequest, roleHeader);

        // Construct response body
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("id", response.getId());
        responseBody.put("firstName", response.getFirstName());
        responseBody.put("surname", response.getSurname());
        responseBody.put("roleId", mapRoleToRoleId(response.getRole().name()));

        logger.info("Successfully updated employee with ID: {}", response.getId());
        return ResponseEntity.ok(responseBody);
    }

    /**
     * Deletes an employee. Only users with the ADMIN role are authorized.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an employee",
            description = "Deletes an employee. Only ADMIN roles are authorized.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid role"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteEmployee(@PathVariable Long id) {
        logger.info("Received request to delete employee with ID: {}", id);
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(Map.of("message", "Employee deleted successfully"));
    }

    /**
     * Maps a role string to its corresponding role ID.
     */
    private Long mapRoleToRoleId(String role) {
        return switch (role.toUpperCase()) {
            case "ADMIN" -> 1L;
            case "USER" -> 2L;
            case "MANAGER" -> 3L;
            default -> throw new InvalidRoleException("Invalid role: " + role);
        };
    }
}
