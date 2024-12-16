package com.naiomi.employee.data.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naiomi.employee.data.dto.EmployeeApiRequestDto;
import com.naiomi.employee.data.dto.EmployeeApiResponseDto;
import com.naiomi.employee.data.exception.GlobalExceptionHandler;
import com.naiomi.employee.data.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EmployeeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private EmployeeApiRequestDto validRequestDto;
    private EmployeeApiResponseDto validResponseDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Initialize MockMvc with the controller and GlobalExceptionHandler
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController)
                .setControllerAdvice(new GlobalExceptionHandler()) // Register exception handler
                .build();

        // Initialize valid request and response DTOs
        validRequestDto = new EmployeeApiRequestDto();
        validRequestDto.setName("John Doe");
        validRequestDto.setRoleId(1L);

        validResponseDto = new EmployeeApiResponseDto();
        validResponseDto.setId(1L);
        validResponseDto.setName("John Doe");
        validResponseDto.setRoleId(1L);
    }


    @Test
    @DisplayName("Create Employee - Success")
    void testCreateEmployee_Success() throws Exception {
        // Mock service behavior
        when(employeeService.createEmployee(any(EmployeeApiRequestDto.class))).thenReturn(validResponseDto);

        // Perform POST request with JSON data
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.roleId").value(1));
    }

    @Test
    @DisplayName("Create Employee - Invalid Input")
    void testCreateEmployee_InvalidInput() throws Exception {
        // Perform POST request with invalid JSON data
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\", \"roleId\":null}")) // Invalid name and roleId
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name must not be blank"))
                .andExpect(jsonPath("$.roleId").value("Role ID must not be null"));
    }

    @Test
    @DisplayName("Update Employee - Success")
    void testUpdateEmployee_Success() throws Exception {
        // Mock service behavior
        when(employeeService.updateEmployee(eq(1L), any(EmployeeApiRequestDto.class))).thenReturn(validResponseDto);

        // Perform PUT request with JSON data
        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.roleId").value(1));
    }

    @Test
    @DisplayName("Update Employee - Invalid Input")
    void testUpdateEmployee_InvalidInput() throws Exception {
        // Perform PUT request with invalid JSON data
        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\", \"roleId\":null}")) // Invalid name and roleId
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name must not be blank"))
                .andExpect(jsonPath("$.roleId").value("Role ID must not be null"));
    }

    @Test
    @DisplayName("Get Employee By ID - Success")
    void testGetEmployeeById() throws Exception {
        // Mock response
        when(employeeService.getEmployeeById(1L)).thenReturn(validResponseDto);

        // Perform GET request
        mockMvc.perform(get("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.roleId").value(1));
    }

    @Test
    @DisplayName("Get Employee By ID - Not Found")
    void testGetEmployeeById_NotFound() throws Exception {
        // Mock service behavior to throw exception
        when(employeeService.getEmployeeById(1L))
                .thenThrow(new IllegalArgumentException("Employee not found with ID: 1"));

        // Perform GET request
        mockMvc.perform(get("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Employee not found with ID: 1"));
    }

    @Test
    void testDeleteEmployee_Success() throws Exception {
        Long employeeId = 1L;

        // Mock service behavior
        doNothing().when(employeeService).deleteEmployeeById(employeeId);

        // Perform DELETE request
        mockMvc.perform(delete("/api/employees/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\": \"Employee deleted successfully\"}"));

        // Verify service interaction
        Mockito.verify(employeeService).deleteEmployeeById(employeeId);
    }

    @Test
    void testDeleteEmployee_NotFound() throws Exception {
        Long employeeId = 1L;

        // Mock service behavior
        doThrow(new IllegalArgumentException("Employee with ID " + employeeId + " does not exist"))
                .when(employeeService).deleteEmployeeById(employeeId);

        // Perform DELETE request
        mockMvc.perform(delete("/api/employees/{id}", employeeId))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"Employee with ID 1 does not exist\"}"));

        // Verify service interaction
        Mockito.verify(employeeService).deleteEmployeeById(employeeId);
    }


}
