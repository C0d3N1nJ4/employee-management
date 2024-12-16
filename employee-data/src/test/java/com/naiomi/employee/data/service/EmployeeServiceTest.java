package com.naiomi.employee.data.service;

import com.naiomi.employee.data.constant.RoleType;
import com.naiomi.employee.data.dto.EmployeeApiRequestDto;
import com.naiomi.employee.data.dto.EmployeeApiResponseDto;
import com.naiomi.employee.data.mapper.EmployeeMapper;
import com.naiomi.employee.data.model.Employee;
import com.naiomi.employee.data.model.Role;
import com.naiomi.employee.data.repository.EmployeeRepository;
import com.naiomi.employee.data.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeService employeeService;

    private Role mockRole;
    private Employee mockEmployee;

    @BeforeEach
    void setup() {
        // Mock role
        mockRole = new Role();
        mockRole.setId(1L);
        mockRole.setName(RoleType.ADMIN);

        // Mock employee
        mockEmployee = new Employee();
        mockEmployee.setId(1L);
        mockEmployee.setFirstname("John");
        mockEmployee.setSurname("Doe");
        mockEmployee.setRole(mockRole);
    }

    @Test
    @DisplayName("Should create a new employee with valid input and role")
    void testCreateEmployee() {
        // Mock dependencies
        when(roleRepository.findById(1L)).thenReturn(Optional.of(mockRole));
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

        // Mock mapper response
        EmployeeApiResponseDto expectedResponse = new EmployeeApiResponseDto();
        expectedResponse.setId(1L);
        expectedResponse.setName("John Doe");
        expectedResponse.setRoleId(1L);

        when(employeeMapper.toResponseDto(any(Employee.class))).thenReturn(expectedResponse);

        // Input DTO
        EmployeeApiRequestDto requestDto = new EmployeeApiRequestDto();
        requestDto.setName("John Doe");
        requestDto.setRoleId(1L);

        // Call the service method
        EmployeeApiResponseDto actualResponse = employeeService.createEmployee(requestDto);

        // Assertions
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getRoleId(), actualResponse.getRoleId());

        // Verify interactions
        verify(roleRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).save(any(Employee.class));
        verify(employeeMapper, times(1)).toResponseDto(any(Employee.class));
    }

    @Test
    @DisplayName("Should update employee details and role successfully")
    void testUpdateEmployee() {
        // Mock dependencies
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(mockRole));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock mapper response
        EmployeeApiResponseDto expectedResponse = new EmployeeApiResponseDto();
        expectedResponse.setId(1L);
        expectedResponse.setName("Jane Doe");
        expectedResponse.setRoleId(2L);

        when(employeeMapper.toResponseDto(any(Employee.class))).thenReturn(expectedResponse);

        // Input DTO
        EmployeeApiRequestDto requestDto = new EmployeeApiRequestDto();
        requestDto.setName("Jane Doe");
        requestDto.setRoleId(2L);

        // Call the service method
        EmployeeApiResponseDto actualResponse = employeeService.updateEmployee(1L, requestDto);

        // Assertions
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getRoleId(), actualResponse.getRoleId());

        // Verify interactions
        verify(employeeRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).findById(2L);
        verify(employeeMapper, times(1)).updateEmployeeFromRequestDto(eq(requestDto), any(Employee.class));
        verify(employeeRepository, times(1)).save(any(Employee.class));
        verify(employeeMapper, times(1)).toResponseDto(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw exception when role is not found during employee creation")
    void testCreateEmployee_RoleNotFound() {
        // Mock role repository to return empty
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        // Input DTO
        EmployeeApiRequestDto requestDto = new EmployeeApiRequestDto();
        requestDto.setName("John Doe");
        requestDto.setRoleId(1L);

        // Call service and expect exception
        assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(requestDto));
    }

}
