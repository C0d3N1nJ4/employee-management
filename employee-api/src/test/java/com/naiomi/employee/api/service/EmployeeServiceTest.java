package com.naiomi.employee.api.service;

import com.naiomi.employee.api.dto.EmployeeApiRequestDto;
import com.naiomi.employee.api.dto.EmployeeApiResponseDto;
import com.naiomi.employee.api.dto.EmployeeDataRequestDto;
import com.naiomi.employee.api.dto.EmployeeDataResponseDto;
import com.naiomi.employee.api.exception.EmployeeNotFoundException;
import com.naiomi.employee.api.exception.GlobalExceptionHandler;
import com.naiomi.employee.api.mapper.EmployeeMapper;
import com.naiomi.employee.api.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmployeeService employeeService;

    private final String employeeDataUrl = "http://localhost:9091/api/employees";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeService = new EmployeeService(employeeMapper, restTemplate, employeeDataUrl);
    }

    @Test
    @DisplayName("Create Employee: Valid Request")
    void createEmployee_ValidRequest() {
        EmployeeApiRequestDto apiRequest = new EmployeeApiRequestDto();
        apiRequest.setFirstName("John");
        apiRequest.setSurname("Doe");
        apiRequest.setRole("ADMIN");

        EmployeeDataRequestDto app2Request = new EmployeeDataRequestDto("John Doe", 1L);
        EmployeeDataResponseDto app2Response = new EmployeeDataResponseDto(1L, "John Doe", 1L);

        when(employeeMapper.toEmployeeDataRequest(apiRequest)).thenReturn(app2Request);
        when(restTemplate.postForObject(employeeDataUrl, app2Request, EmployeeDataResponseDto.class)).thenReturn(app2Response);
        when(employeeMapper.toApp1Response(app2Response)).thenReturn(new EmployeeApiResponseDto(1L, "John", "Doe", Role.ADMIN));

        EmployeeApiResponseDto response = employeeService.createEmployee(apiRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getSurname());
        assertEquals(Role.ADMIN, response.getRole());

        verify(restTemplate).postForObject(employeeDataUrl, app2Request, EmployeeDataResponseDto.class);
        verify(employeeMapper).toApp1Response(app2Response);
    }

    @Test
    @DisplayName("Create Employee: Invalid Role")
    void createEmployee_InvalidRole() {
        EmployeeApiRequestDto apiRequest = new EmployeeApiRequestDto();
        apiRequest.setFirstName("John");
        apiRequest.setSurname("Doe");
        apiRequest.setRole("INVALID_ROLE");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(apiRequest));
        assertEquals("Invalid role: INVALID_ROLE. Allowed roles are ADMIN, USER, MANAGER", exception.getMessage());

        verifyNoInteractions(employeeMapper);
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Create Employee: Invalid Response from App2")
    void createEmployee_InvalidResponseFromApp2() {
        // Arrange
        EmployeeApiRequestDto apiRequest = new EmployeeApiRequestDto();
        apiRequest.setFirstName("John");
        apiRequest.setSurname("Doe");
        apiRequest.setRole("ADMIN");

        EmployeeDataRequestDto app2Request = new EmployeeDataRequestDto("John Doe", 1L);

        when(employeeMapper.toEmployeeDataRequest(apiRequest)).thenReturn(app2Request);
        when(restTemplate.postForObject(employeeDataUrl, app2Request, EmployeeDataResponseDto.class)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.createEmployee(apiRequest));
        assertEquals("Invalid response from App2: Response is null.", exception.getMessage());

        // Verify interactions
        verify(employeeMapper).toEmployeeDataRequest(apiRequest);
        verify(restTemplate).postForObject(employeeDataUrl, app2Request, EmployeeDataResponseDto.class);
    }



    @Test
    @DisplayName("Get Employee by ID: Valid ID")
    void getEmployeeById_ValidId() {
        Long id = 1L;
        EmployeeDataResponseDto app2Response = new EmployeeDataResponseDto(1L, "John Doe", 1L);

        when(restTemplate.getForObject(employeeDataUrl + "/" + id, EmployeeDataResponseDto.class)).thenReturn(app2Response);
        when(employeeMapper.toApp1Response(app2Response)).thenReturn(new EmployeeApiResponseDto(1L, "John", "Doe", Role.ADMIN));

        EmployeeApiResponseDto response = employeeService.getEmployeeById(id);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getSurname());
        assertEquals(Role.ADMIN, response.getRole());

        verify(restTemplate).getForObject(employeeDataUrl + "/" + id, EmployeeDataResponseDto.class);
        verify(employeeMapper).toApp1Response(app2Response);
    }

    @Test
    @DisplayName("Get Employee by ID: Employee Not Found")
    void getEmployeeById_NotFound() {
        Long id = 1L;

        // Mock the exception
        when(restTemplate.getForObject(employeeDataUrl + "/" + id, EmployeeDataResponseDto.class))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.NOT_FOUND,
                        "Not Found",
                        null,
                        null,
                        null
                ));

        // Act & Assert
        EmployeeNotFoundException thrownException = assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.getEmployeeById(id));
        assertEquals("Employee not found with ID: " + id, thrownException.getMessage());

        // Verify interactions
        verify(restTemplate).getForObject(employeeDataUrl + "/" + id, EmployeeDataResponseDto.class);
        verifyNoInteractions(employeeMapper);
    }




    @Test
    @DisplayName("Update Employee: Valid Request")
    void updateEmployee_ValidRequest() {
        Long id = 1L;
        EmployeeApiRequestDto apiRequest = new EmployeeApiRequestDto();
        apiRequest.setFirstName("Jane");
        apiRequest.setSurname("Doe");
        apiRequest.setRole("USER");

        EmployeeDataRequestDto app2Request = new EmployeeDataRequestDto("Jane Doe", 2L);
        EmployeeDataResponseDto app2Response = new EmployeeDataResponseDto(1L, "Jane Doe", 2L);

        when(employeeMapper.toEmployeeDataRequest(apiRequest)).thenReturn(app2Request);
        when(restTemplate.exchange(eq(employeeDataUrl + "/" + id), eq(HttpMethod.PUT), any(HttpEntity.class), eq(EmployeeDataResponseDto.class)))
                .thenReturn(ResponseEntity.ok(app2Response));
        when(employeeMapper.toApp1Response(app2Response)).thenReturn(new EmployeeApiResponseDto(1L, "Jane", "Doe", Role.USER));

        EmployeeApiResponseDto response = employeeService.updateEmployee(id, apiRequest, "USER");

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Jane", response.getFirstName());
        assertEquals("Doe", response.getSurname());
        assertEquals(Role.USER, response.getRole());

        verify(restTemplate).exchange(eq(employeeDataUrl + "/" + id), eq(HttpMethod.PUT), any(HttpEntity.class), eq(EmployeeDataResponseDto.class));
        verify(employeeMapper).toApp1Response(app2Response);
    }

    @Test
    @DisplayName("Delete Employee: Valid ID")
    void deleteEmployee_ValidId() {
        Long id = 1L;

        doNothing().when(restTemplate).delete(employeeDataUrl + "/" + id);

        assertDoesNotThrow(() -> employeeService.deleteEmployee(id));

        verify(restTemplate).delete(employeeDataUrl + "/" + id);
    }

    @Test
    @DisplayName("Delete Employee: Employee Not Found")
    void deleteEmployee_NotFound() {
        Long id = 1L;

        // Mock the exception
        doThrow(HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                null,
                null,
                null
        )).when(restTemplate).delete(employeeDataUrl + "/" + id);

        // Act & Assert
        EmployeeNotFoundException thrownException = assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.deleteEmployee(id));
        assertEquals("Employee not found with ID: " + id, thrownException.getMessage());

        // Verify interactions
        verify(restTemplate).delete(employeeDataUrl + "/" + id);
    }


    @Test
    @DisplayName("Create Employee: Valid Role Maps to Correct Role ID")
    void createEmployee_ValidRoleMapsToRoleId() {
        EmployeeApiRequestDto apiRequest = new EmployeeApiRequestDto();
        apiRequest.setFirstName("John");
        apiRequest.setSurname("Doe");
        apiRequest.setRole("ADMIN");

        EmployeeDataRequestDto expectedApp2Request = new EmployeeDataRequestDto("John Doe", 1L);
        EmployeeDataResponseDto app2Response = new EmployeeDataResponseDto(1L, "John Doe", 1L);

        when(employeeMapper.toEmployeeDataRequest(apiRequest)).thenReturn(expectedApp2Request);
        when(restTemplate.postForObject(anyString(), eq(expectedApp2Request), eq(EmployeeDataResponseDto.class)))
                .thenReturn(app2Response);
        when(employeeMapper.toApp1Response(app2Response))
                .thenReturn(new EmployeeApiResponseDto(1L, "John", "Doe", Role.ADMIN));

        EmployeeApiResponseDto response = employeeService.createEmployee(apiRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(Role.ADMIN, response.getRole());

        verify(employeeMapper).toEmployeeDataRequest(apiRequest);
        verify(restTemplate).postForObject(anyString(), eq(expectedApp2Request), eq(EmployeeDataResponseDto.class));
    }

    @Test
    @DisplayName("Handle EmployeeNotFoundException")
    void handleEmployeeNotFoundException() {
        // Arrange
        String errorMessage = "Employee not found with ID: 1";
        EmployeeNotFoundException exception = new EmployeeNotFoundException(errorMessage);

        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleEmployeeNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
        assertEquals(404, response.getBody().get("status"));
    }

}
