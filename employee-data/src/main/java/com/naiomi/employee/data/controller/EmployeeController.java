package com.naiomi.employee.data.controller;

import com.naiomi.employee.data.dto.EmployeeApiRequestDto;
import com.naiomi.employee.data.dto.EmployeeApiResponseDto;
import com.naiomi.employee.data.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for managing employees.
 * Provides endpoints to create, update, retrieve, and delete employees.
 */
@RestController
@RequestMapping("/api/employees")
@Validated
@Tag(name = "Employee Controller", description = "Endpoints for managing employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @Operation(summary = "Create a new employee", description = "Creates a new employee with the given details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee created successfully",
                    content = @Content(schema = @Schema(implementation = EmployeeApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation errors",
                    content = @Content(schema = @Schema(example = "{ 'field': 'error message' }"))),
            @ApiResponse(responseCode = "500", description = "Unexpected error occurred")
    })
    public ResponseEntity<?> createEmployee(
            @Valid @RequestBody EmployeeApiRequestDto requestDto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        EmployeeApiResponseDto responseDto = employeeService.createEmployee(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an employee", description = "Updates the details of an existing employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee updated successfully",
                    content = @Content(schema = @Schema(implementation = EmployeeApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation errors"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<?> updateEmployee(
            @Parameter(description = "ID of the employee to update", required = true) @PathVariable Long id,
            @Valid @RequestBody EmployeeApiRequestDto requestDto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        EmployeeApiResponseDto responseDto = employeeService.updateEmployee(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an employee by ID", description = "Retrieves the details of an employee by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee retrieved successfully",
                    content = @Content(schema = @Schema(implementation = EmployeeApiResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<EmployeeApiResponseDto> getEmployeeById(
            @Parameter(description = "ID of the employee to retrieve", required = true) @PathVariable Long id) {
        EmployeeApiResponseDto responseDto = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an employee", description = "Deletes an employee by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee deleted successfully",
                    content = @Content(schema = @Schema(example = "{ 'message': 'Employee deleted successfully' }"))),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<?> deleteEmployee(
            @Parameter(description = "ID of the employee to delete", required = true) @PathVariable Long id) {
        employeeService.deleteEmployeeById(id);
        return ResponseEntity.ok().body(Map.of("message", "Employee deleted successfully"));
    }
}
