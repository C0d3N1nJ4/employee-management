package com.naiomi.employee.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) for creating or updating an Employee.
 * Contains the employee's name and the associated role ID.
 */
public class EmployeeApiRequestDto {

    /**
     * The name of the employee.
     * <p>
     * This field is mandatory and cannot be blank.
     * </p>
     */
    @NotBlank(message = "Name must not be blank")
    private String name;

    /**
     * The ID of the role assigned to the employee.
     * <p>
     * This field is mandatory and cannot be null.
     * </p>
     */
    @NotNull(message = "Role ID must not be null")
    private Long roleId;

    /**
     * Gets the name of the employee.
     *
     * @return the employee's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the employee.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the role ID of the employee.
     *
     * @return the role ID
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * Sets the role ID of the employee.
     *
     * @param roleId the role ID to set
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
