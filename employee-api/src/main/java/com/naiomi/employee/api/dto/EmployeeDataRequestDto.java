package com.naiomi.employee.api.dto;

public class EmployeeDataRequestDto {

    private String name;
    private Long roleId;

    public EmployeeDataRequestDto(String name, Long roleId) {
        this.name = name;
        this.roleId = roleId;
    }

    public EmployeeDataRequestDto() {}

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