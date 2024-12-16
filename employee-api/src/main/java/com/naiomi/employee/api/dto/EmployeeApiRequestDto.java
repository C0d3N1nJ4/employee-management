package com.naiomi.employee.api.dto;

import com.naiomi.employee.api.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EmployeeApiRequestDto {

    @NotBlank(message = "First name is required.")
    private String firstName;

    @NotBlank(message = "Surname is required.")
    private String surname;

    @NotBlank(message = "Role is required.")
    @Size(min = 3, max = 50, message = "Role must be between 3 and 50 characters.")
    private String role;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Role getValidatedRole() {
        return Role.fromString(this.role);
    }
}