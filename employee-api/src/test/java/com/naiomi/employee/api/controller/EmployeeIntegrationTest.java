package com.naiomi.employee.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for App1 endpoints, mocking App2 with MockRestServiceServer.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc    // Important: let Spring Boot auto-configure MockMvc + Security
class EmployeeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;  // Injected by Spring because of @AutoConfigureMockMvc

    @Autowired
    private RestTemplate restTemplate;

    @Value("${employee.data.url}")
    private String employeeDataUrl;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        // Create a MockRestServiceServer to mock external calls to App2
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    // --- CREATE EMPLOYEE TESTS ---

    @Test
    @DisplayName("Create Employee: Retry and Successful After Failure")
    void createEmployee_RetryAndSuccess() throws Exception {
        // Simulate two failures followed by a success
        mockServer.expect(requestTo(employeeDataUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)); // 1st failure

        mockServer.expect(requestTo(employeeDataUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)); // 2nd failure

        mockServer.expect(requestTo(employeeDataUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                {
                  "id": 1,
                  "name": "John Doe",
                  "roleId": 1
                }
                """, MediaType.APPLICATION_JSON)); // 3rd attempt success

        mockMvc.perform(post("/employees")
                        .header("Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "firstName": "John",
                          "surname": "Doe"
                        }
                        """))
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.surname", is("Doe")))
                .andExpect(jsonPath("$.roleId", is(1)));

        mockServer.verify();
    }

    @Test
    @DisplayName("Create Employee: Invalid Response from App2")
    void createEmployee_InvalidResponseFromApp2() throws Exception {
        // All 3 attempts return invalid response => final internal server error
        mockServer.expect(ExpectedCount.times(3), requestTo(employeeDataUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
            {
              "id": null,
              "name": null,
              "roleId": 1
            }
            """, MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/employees")
                        .header("Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "firstName": "John",
                      "surname": "Doe"
                    }
                    """))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is("Failed to create employee after retries: John")));

        mockServer.verify();
    }



    @Test
    @DisplayName("Create Employee: Unauthorized Role")
    void createEmployee_UnauthorizedRole() throws Exception {
        // "USER" is valid but not permitted => 403
        mockMvc.perform(post("/employees")
                        .header("Role", "USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "firstName": "John",
                          "surname": "Doe"
                        }
                        """))
                .andExpect(status().isForbidden())  // 403
                .andExpect(jsonPath("$.error", is("Access Denied")));
    }

    // --- GET EMPLOYEE TESTS ---

    @Test
    @DisplayName("Get Employee: Valid ID")
    void getEmployee_ValidId() throws Exception {
        Long employeeId = 1L;
        mockServer.expect(requestTo(employeeDataUrl + "/" + employeeId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                {
                  "id": 1,
                  "name": "Naiomi Naidoo",
                  "roleId": 1
                }
                """, MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/employees/{id}", employeeId)
                        .header("Role", "USER")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Naiomi")))
                .andExpect(jsonPath("$.surname", is("Naidoo")))
                // For role, presumably roleId=1 => ADMIN
                .andExpect(jsonPath("$.role", is("ADMIN")));

        mockServer.verify();
    }

    @Test
    @DisplayName("Get Employee: Not Found")
    void getEmployee_NotFound() throws Exception {
        Long employeeId = 99L;
        mockServer.expect(requestTo(employeeDataUrl + "/" + employeeId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/employees/{id}", employeeId)
                        .header("Role", "USER")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Employee not found with ID: " + employeeId)));

        mockServer.verify();
    }

    // --- UPDATE EMPLOYEE TESTS ---

    @Test
    @DisplayName("Update Employee: Valid Input")
    void updateEmployee_ValidInput() throws Exception {
        Long employeeId = 1L;
        String url = employeeDataUrl + "/" + employeeId;

        mockServer.expect(requestTo(url))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess("""
                {
                  "id": 1,
                  "name": "Jane Doe",
                  "roleId": 2
                }
                """, MediaType.APPLICATION_JSON));

        mockMvc.perform(put("/employees/{id}", employeeId)
                        .header("Role", "USER") // valid for update
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "firstName": "Jane",
                          "surname": "Doe"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Jane")))
                .andExpect(jsonPath("$.surname", is("Doe")))
                .andExpect(jsonPath("$.roleId", is(2)));

        mockServer.verify();
    }

    // --- DELETE EMPLOYEE TESTS ---

    @Test
    @DisplayName("Delete Employee: Successful Deletion")
    void deleteEmployee_SuccessfulDeletion() throws Exception {
        Long employeeId = 1L;
        String url = employeeDataUrl + "/" + employeeId;

        mockServer.expect(requestTo(url))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        mockMvc.perform(delete("/employees/{id}", employeeId)
                        .header("Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Employee deleted successfully")));

        mockServer.verify();
    }

    @Test
    @DisplayName("Delete Employee: Unauthorized Role")
    void deleteEmployee_UnauthorizedRole() throws Exception {
        // "USER" is valid but not authorized => 403
        Long employeeId = 1L;

        mockMvc.perform(delete("/employees/{id}", employeeId)
                        .header("Role", "USER"))
                .andExpect(status().isForbidden())   // 403
                .andExpect(jsonPath("$.error", is("Access Denied")));

        mockServer.verify();
    }
}
