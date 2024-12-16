package com.naiomi.employee.api.controller;

import com.naiomi.employee.api.dto.EmployeeApiRequestDto;
import com.naiomi.employee.api.dto.EmployeeApiResponseDto;
import com.naiomi.employee.api.exception.GlobalExceptionHandler;
import com.naiomi.employee.api.model.Role;
import com.naiomi.employee.api.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;  // Injected by Spring

    @MockBean
    private EmployeeService employeeService;

    private final String validRequestBody = """
            {
              "firstName": "John",
              "surname": "Doe"
            }
            """;

    private final EmployeeApiResponseDto validResponse = new EmployeeApiResponseDto(1L, "John", "Doe", Role.ADMIN);

    @Test
    @DisplayName("Create Employee - Valid Input (ADMIN Role)")
    void createEmployee_ValidInput() throws Exception {
        // Arrange
        Mockito.when(employeeService.createEmployee(any(EmployeeApiRequestDto.class)))
                .thenReturn(validResponse);

        // Act & Assert
        mockMvc.perform(post("/employees")
                        .header("Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.surname", is("Doe")))
                .andExpect(jsonPath("$.roleId", is(1)));
    }

    @Test
    @DisplayName("Create Employee - Invalid Role")
    void createEmployee_InvalidRole() throws Exception {
        // "GUEST" is outside [ADMIN, USER, MANAGER], so your custom validation returns 400
        mockMvc.perform(post("/employees")
                        .header("Role", "GUEST")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid role: GUEST. Allowed roles are: [ADMIN, USER, MANAGER]"));

    }

    @Test
    @DisplayName("Create Employee - Missing Role Header")
    void createEmployee_MissingRoleHeader() throws Exception {
        // Missing "Role" header → 400 per your custom validation logic
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Role is required and cannot be null or blank. Allowed roles are ADMIN, USER, MANAGER."));
    }

    @Test
    @DisplayName("Delete Employee - Unauthorized Role")
    void deleteEmployee_UnauthorizedRole() throws Exception {
        // "USER" is a valid role, but not authorized for DELETE (needs ADMIN).
        // With @PreAuthorize("hasAuthority('ADMIN')"), this yields 403
        Long employeeId = 1L;
        mockMvc.perform(delete("/employees/{id}", employeeId)
                        .header("Role", "USER"))
                .andExpect(status().isForbidden()) // 403
                // Spring Security's default JSON might differ, so you may need a custom AccessDeniedHandler
                .andExpect(jsonPath("$.error").value("Access Denied"));
    }

    @Test
    @DisplayName("Delete Employee - Valid Request")
    void deleteEmployee_ValidRequest() throws Exception {
        Long employeeId = 1L;
        Mockito.doNothing().when(employeeService).deleteEmployee(employeeId);

        mockMvc.perform(delete("/employees/{id}", employeeId)
                        .header("Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee deleted successfully"));
    }

    @Test
    @DisplayName("Get Employee by ID - Invalid Role")
    void getEmployeeById_InvalidRole() throws Exception {
        // "INVALID_ROLE" is outside [ADMIN, USER, MANAGER] → 400
        Long employeeId = 1L;
        mockMvc.perform(get("/employees/{id}", employeeId)
                        .header("Role", "INVALID_ROLE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid role: INVALID_ROLE. Allowed roles are: [ADMIN, USER, MANAGER]"));
    }

    @Test
    @DisplayName("Update Employee - Missing Role Header")
    void updateEmployee_MissingRoleHeader() throws Exception {
        // Missing "Role" header → 400
        Long employeeId = 1L;
        String updateRequestBody = """
                {
                  "firstName": "Jane",
                  "surname": "Doe"
                }
                """;

        mockMvc.perform(put("/employees/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Role is required and cannot be null or blank. Allowed roles are ADMIN, USER, MANAGER."));

    }

    @Test
    @DisplayName("Update Employee - Unauthorized Role")
    void updateEmployee_UnauthorizedRole() throws Exception {
        // "ADMIN" is valid but not authorized for updates (method-level security requires USER)
        Long employeeId = 1L;
        String updateRequestBody = """
                {
                  "firstName": "Jane",
                  "surname": "Doe"
                }
                """;

        mockMvc.perform(put("/employees/{id}", employeeId)
                        .header("Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestBody))
                .andExpect(status().isForbidden()) // 403
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    @DisplayName("Get Employee by ID - Valid Request")
    void getEmployeeById_ValidRequest() throws Exception {
        // "USER" is valid for GET
        Long employeeId = 1L;
        Mockito.when(employeeService.getEmployeeById(employeeId))
                .thenReturn(validResponse);

        mockMvc.perform(get("/employees/{id}", employeeId)
                        .header("Role", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.surname", is("Doe")));
    }
}
