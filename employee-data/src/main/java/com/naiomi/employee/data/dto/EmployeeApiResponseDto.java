package com.naiomi.employee.data.dto;

/**
 * Data Transfer Object (DTO) for employee API responses.
 * Contains the employee's ID, name, and associated role ID.
 */
public class EmployeeApiResponseDto {

    /**
     * The unique identifier for the employee.
     */
    private Long id;

    /**
     * The full name of the employee.
     */
    private String name;

    /**
     * The unique identifier for the role assigned to the employee.
     */
    private Long roleId;

    /**
     * Gets the unique identifier of the employee.
     *
     * @return the employee's ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the employee.
     *
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

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
     * Gets the unique identifier of the role assigned to the employee.
     *
     * @return the role ID
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * Sets the unique identifier of the role assigned to the employee.
     *
     * @param roleId the role ID to set
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
