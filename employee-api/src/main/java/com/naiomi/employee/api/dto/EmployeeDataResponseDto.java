package com.naiomi.employee.api.dto;

public class EmployeeDataResponseDto {

    private Long id;
    private String name;
    private Long roleId;

    public EmployeeDataResponseDto(Long id, String name, Long roleId) {
        this.id = id;
        this.name = name;
        this.roleId = roleId;
    }

    public EmployeeDataResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}