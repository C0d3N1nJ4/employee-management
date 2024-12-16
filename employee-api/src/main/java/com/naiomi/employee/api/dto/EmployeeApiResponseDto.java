package com.naiomi.employee.api.dto;

import com.naiomi.employee.api.model.Role;

public class EmployeeApiResponseDto {

    private Long id;
    private String firstName;
    private String surname;
    private Role role;

    public EmployeeApiResponseDto(Long id, String firstName, String surname, Role role) {
        this.id = id;
        this.firstName = firstName;
        this.surname = surname;
        this.role = role;
    }

    public EmployeeApiResponseDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}