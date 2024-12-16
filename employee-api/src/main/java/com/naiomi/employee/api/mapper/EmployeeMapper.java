package com.naiomi.employee.api.mapper;

import com.naiomi.employee.api.dto.EmployeeApiRequestDto;
import com.naiomi.employee.api.dto.EmployeeApiResponseDto;
import com.naiomi.employee.api.dto.EmployeeDataRequestDto;
import com.naiomi.employee.api.dto.EmployeeDataResponseDto;
import com.naiomi.employee.api.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "name", expression = "java(employee.getFirstName() + \" \" + employee.getSurname())")
    @Mapping(target = "roleId", expression = "java(mapRoleToRoleId(employee.getValidatedRole()))")
    EmployeeDataRequestDto toEmployeeDataRequest(EmployeeApiRequestDto employee);

    @Mapping(target = "firstName", expression = "java(splitName(dataResponse.getName(), 0))")
    @Mapping(target = "surname", expression = "java(splitName(dataResponse.getName(), 1))")
    @Mapping(target = "role", expression = "java(mapRoleIdToRole(dataResponse.getRoleId()))")
    EmployeeApiResponseDto toApp1Response(EmployeeDataResponseDto dataResponse);

    // Utility methods for mapping roles and splitting names
    default Long mapRoleToRoleId(Role role) {
        return switch (role) {
            case ADMIN -> 1L;
            case USER -> 2L;
            case MANAGER -> 3L;
        };
    }

    default Role mapRoleIdToRole(Long roleId) {
        return switch (roleId.intValue()) {
            case 1 -> Role.ADMIN;
            case 2 -> Role.USER;
            case 3 -> Role.MANAGER;
            default -> throw new IllegalArgumentException("Invalid role ID: " + roleId);
        };
    }

    default String splitName(String name, int index) {
        String[] parts = (name != null) ? name.split(" ", 2) : new String[]{"", ""};
        return (index < parts.length) ? parts[index] : "";
    }
}
