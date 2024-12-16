package com.naiomi.employee.data.mapper;

import com.naiomi.employee.data.dto.EmployeeApiRequestDto;
import com.naiomi.employee.data.dto.EmployeeApiResponseDto;
import com.naiomi.employee.data.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeMapperTest {

    private EmployeeMapper employeeMapper;

    @BeforeEach
    void setUp() {
        employeeMapper = Mappers.getMapper(EmployeeMapper.class);
    }

    @Test
    @DisplayName("Should map Employee to EmployeeApiResponseDto correctly")
    void testToResponseDto() {
        // Prepare test data
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstname("John");
        employee.setSurname("Doe");

        // Perform mapping
        EmployeeApiResponseDto responseDto = employeeMapper.toResponseDto(employee);

        // Assertions
        assertNotNull(responseDto);
        assertEquals(1L, responseDto.getId());
        assertEquals("John Doe", responseDto.getName());
    }

    @Test
    @DisplayName("Should update Employee entity fields from EmployeeApiRequestDto")
    void testUpdateEmployeeFromRequestDto() {
        // Prepare test data
        EmployeeApiRequestDto requestDto = new EmployeeApiRequestDto();
        requestDto.setName("Jane Doe");
        requestDto.setRoleId(2L);

        Employee employee = new Employee();
        employee.setFirstname("John");
        employee.setSurname("Smith");

        // Perform update
        employeeMapper.updateEmployeeFromRequestDto(requestDto, employee);

        // Assertions
        assertNotNull(employee);
        assertEquals("Jane", employee.getFirstname());
        assertEquals("Doe", employee.getSurname());
        assertNull(employee.getRole()); // Role should remain unchanged
    }
}
