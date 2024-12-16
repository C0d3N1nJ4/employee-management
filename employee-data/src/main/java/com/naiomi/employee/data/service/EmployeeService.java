package com.naiomi.employee.data.service;

import com.naiomi.employee.data.dto.EmployeeApiRequestDto;
import com.naiomi.employee.data.dto.EmployeeApiResponseDto;
import com.naiomi.employee.data.mapper.EmployeeMapper;
import com.naiomi.employee.data.model.Employee;
import com.naiomi.employee.data.model.Role;
import com.naiomi.employee.data.repository.EmployeeRepository;
import com.naiomi.employee.data.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeService(EmployeeRepository employeeRepository, RoleRepository roleRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.roleRepository = roleRepository;
        this.employeeMapper = employeeMapper;
    }

    public EmployeeApiResponseDto createEmployee(EmployeeApiRequestDto requestDto) {
        validateRequest(requestDto);

        // Check if role exists
        Role role = getRoleById(requestDto.getRoleId());

        // Map DTO to entity
        Employee employee = mapRequestToEmployee(requestDto, role);

        // Save employee and map to response DTO
        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponseDto(savedEmployee);
    }

    public EmployeeApiResponseDto updateEmployee(Long id, EmployeeApiRequestDto requestDto) {
        validateRequest(requestDto);

        // Fetch the existing employee
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + id));

        // Fetch the new role
        Role role = getRoleById(requestDto.getRoleId());

        // Update fields
        employeeMapper.updateEmployeeFromRequestDto(requestDto, employee);
        employee.setRole(role);

        // Save the updated employee
        Employee updatedEmployee = employeeRepository.save(employee);

        // Return the response DTO
        return employeeMapper.toResponseDto(updatedEmployee);
    }

    public EmployeeApiResponseDto getEmployeeById(Long id) {
        // Fetch the employee by ID
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + id));

        // Map the entity to the response DTO
        return employeeMapper.toResponseDto(employee);
    }

    public void deleteEmployeeById(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new IllegalArgumentException("Employee with ID " + id + " does not exist");
        }
        employeeRepository.deleteById(id);
    }

    // Private Helpers
    private void validateRequest(EmployeeApiRequestDto requestDto) {
        if (requestDto.getName() == null || requestDto.getName().isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        if (requestDto.getRoleId() == null) {
            throw new IllegalArgumentException("Role ID must not be null");
        }
    }

    private Role getRoleById(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + roleId));
    }

    private Employee mapRequestToEmployee(EmployeeApiRequestDto requestDto, Role role) {
        Employee employee = new Employee();
        String[] nameParts = requestDto.getName().split(" ", 2);
        employee.setFirstname(nameParts[0]);
        employee.setSurname(nameParts.length > 1 ? nameParts[1] : "");
        employee.setRole(role);
        return employee;
    }
}
