package com.naiomi.employee.api.mapper;

import com.naiomi.employee.api.dto.EmployeeApiRequestDto;
import com.naiomi.employee.api.dto.EmployeeApiResponseDto;
import com.naiomi.employee.api.dto.EmployeeDataRequestDto;
import com.naiomi.employee.api.dto.EmployeeDataResponseDto;
import com.naiomi.employee.api.model.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeMapperTest {

    private final EmployeeMapper mapper = Mappers.getMapper(EmployeeMapper.class);

    @Test
    @DisplayName("Map EmployeeApiRequestDto to EmployeeDataRequestDto")
    void testToEmployeeDataRequest() {
        // Arrange
        EmployeeApiRequestDto apiRequest = new EmployeeApiRequestDto();
        apiRequest.setFirstName("John");
        apiRequest.setSurname("Doe");
        apiRequest.setRole("ADMIN");

        // Act
        EmployeeDataRequestDto dataRequest = mapper.toEmployeeDataRequest(apiRequest);

        // Assert
        assertNotNull(dataRequest, "Data request should not be null");
        assertEquals("John Doe", dataRequest.getName(), "Name should be a concatenation of first and last name");
        assertEquals(1L, dataRequest.getRoleId(), "Role ID should match ADMIN's ID");
    }

    @Test
    @DisplayName("Map EmployeeDataResponseDto to EmployeeApiResponseDto")
    void testToApp1Response() {
        // Arrange
        EmployeeDataResponseDto dataResponse = new EmployeeDataResponseDto();
        dataResponse.setName("John Doe");
        dataResponse.setRoleId(1L);

        // Act
        EmployeeApiResponseDto apiResponse = mapper.toApp1Response(dataResponse);

        // Assert
        assertNotNull(apiResponse, "API response should not be null");
        assertEquals("John", apiResponse.getFirstName(), "First name should be extracted from the name field");
        assertEquals("Doe", apiResponse.getSurname(), "Surname should be extracted from the name field");
        assertEquals(Role.ADMIN, apiResponse.getRole(), "Role should match ADMIN");
    }

    @Test
    @DisplayName("Map EmployeeDataResponseDto to EmployeeApiResponseDto - Single Name Handling")
    void testToApp1Response_SingleName() {
        // Arrange
        EmployeeDataResponseDto dataResponse = new EmployeeDataResponseDto();
        dataResponse.setName("John");
        dataResponse.setRoleId(2L);

        // Act
        EmployeeApiResponseDto apiResponse = mapper.toApp1Response(dataResponse);

        // Assert
        assertNotNull(apiResponse, "API response should not be null");
        assertEquals("John", apiResponse.getFirstName(), "First name should be extracted from the single name");
        assertEquals("", apiResponse.getSurname(), "Surname should be empty if name has only one word");
        assertEquals(Role.USER, apiResponse.getRole(), "Role should match USER");
    }

    @Test
    @DisplayName("Map EmployeeDataResponseDto to EmployeeApiResponseDto - Null Name Handling")
    void testToApp1Response_NullName() {
        // Arrange
        EmployeeDataResponseDto dataResponse = new EmployeeDataResponseDto();
        dataResponse.setName(null);
        dataResponse.setRoleId(3L);

        // Act
        EmployeeApiResponseDto apiResponse = mapper.toApp1Response(dataResponse);

        // Assert
        assertNotNull(apiResponse, "API response should not be null");
        assertEquals("", apiResponse.getFirstName(), "First name should be empty for null name");
        assertEquals("", apiResponse.getSurname(), "Surname should be empty for null name");
        assertEquals(Role.MANAGER, apiResponse.getRole(), "Role should match MANAGER");
    }

    @Test
    @DisplayName("Handle Invalid Role in EmployeeApiRequestDto")
    void testInvalidRoleInApiRequest() {
        // Arrange
        EmployeeApiRequestDto apiRequest = new EmployeeApiRequestDto();
        apiRequest.setFirstName("John");
        apiRequest.setSurname("Doe");
        apiRequest.setRole("INVALID_ROLE");

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> mapper.toEmployeeDataRequest(apiRequest));
        assertEquals("Invalid role: INVALID_ROLE. Allowed roles are ADMIN, USER, MANAGER", exception.getMessage());
    }
}
