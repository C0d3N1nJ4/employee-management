package com.naiomi.employee.api.service;

import com.naiomi.employee.api.dto.EmployeeApiRequestDto;
import com.naiomi.employee.api.dto.EmployeeApiResponseDto;
import com.naiomi.employee.api.dto.EmployeeDataRequestDto;
import com.naiomi.employee.api.dto.EmployeeDataResponseDto;
import com.naiomi.employee.api.exception.EmployeeNotFoundException;
import com.naiomi.employee.api.mapper.EmployeeMapper;
import com.naiomi.employee.api.model.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeMapper employeeMapper;
    private final RestTemplate restTemplate;

    @Value("${employee.data.url}")
    private String employeeDataUrl;

    public EmployeeService(EmployeeMapper employeeMapper, RestTemplate restTemplate, @Value("${employee.data.url}") String employeeDataUrl) {
        this.employeeMapper = employeeMapper;
        this.restTemplate = restTemplate;
        this.employeeDataUrl = employeeDataUrl;
    }

    @Retryable(
            value = {RestClientException.class, RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public EmployeeApiResponseDto createEmployee(EmployeeApiRequestDto employeeRequest) {
        logger.info("Creating employee with role: {}", employeeRequest.getRole());

        employeeRequest.getValidatedRole();

        EmployeeDataRequestDto app2Request = employeeMapper.toEmployeeDataRequest(employeeRequest);

        EmployeeDataResponseDto app2Response = restTemplate.postForObject(employeeDataUrl, app2Request, EmployeeDataResponseDto.class);

        validateApp2Response(app2Response);

        return mapApp2ResponseToApiResponse(app2Response);
    }

    public EmployeeApiResponseDto getEmployeeById(Long id) {
        logger.info("Fetching employee by ID: {}", id);
        String url = String.format("%s/%d", employeeDataUrl, id);

        try {
            // Fetch employee data from App2
            EmployeeDataResponseDto app2Response = restTemplate.getForObject(url, EmployeeDataResponseDto.class);

            // Log the raw response for debugging purposes
            logger.info("Received response from App2: {}", app2Response);

            // Ensure the response from App2 is valid
            validateApp2Response(app2Response);

            // Log the mapped response
            logger.info("Mapped response: {}", app2Response);

            // Transform App2 response to App1 response
            return mapApp2ResponseToApiResponse(app2Response);

        } catch (HttpClientErrorException.NotFound e) {
            logger.error("Employee with ID {} not found", id);
            throw new EmployeeNotFoundException("Employee not found with ID: " + id);
        } catch (Exception e) {
            logger.error("Error occurred while fetching employee with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Unexpected error while fetching employee: " + e.getMessage());
        }
    }




    public EmployeeApiResponseDto updateEmployee(Long id, EmployeeApiRequestDto employeeRequest, String role) {
        logger.info("Updating employee with ID: {} for role: {}", id, role);

        // Validate the role
        Role validatedRole = Role.fromString(role);
        employeeRequest.setRole(validatedRole.name());

        // Map the request using EmployeeMapper
        EmployeeDataRequestDto app2Request = employeeMapper.toEmployeeDataRequest(employeeRequest);

        // Create the URL for the PUT request
        String url = String.format("%s/%d", employeeDataUrl, id);

        // Perform the PUT request to App2
        ResponseEntity<EmployeeDataResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(app2Request),
                EmployeeDataResponseDto.class
        );

        // Extract the response body
        EmployeeDataResponseDto app2Response = responseEntity.getBody();

        // Validate the response
        validateApp2Response(app2Response);

        // Map App2 response to App1 response and return
        return mapApp2ResponseToApiResponse(app2Response);
    }



    public void deleteEmployee(Long id) {
        logger.info("Deleting employee with ID: {}", id);
        String url = String.format("%s/%d", employeeDataUrl, id);

        try {
            restTemplate.delete(url);
            logger.info("Employee with ID {} deleted successfully", id);
        } catch (HttpClientErrorException.NotFound e) {
            logger.error("Employee with ID {} not found", id);
            throw new EmployeeNotFoundException("Employee not found with ID: " + id);
        } catch (HttpClientErrorException e) {
            logger.error("HTTP error while deleting employee with ID {}: {}", id, e.getStatusCode());
            throw new RuntimeException("Unexpected HTTP error while deleting employee: " + e.getStatusCode());
        } catch (Exception e) {
            logger.error("Unexpected error while deleting employee with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Unexpected error while deleting employee: " + e.getMessage());
        }
    }

    @Recover
    public EmployeeApiResponseDto recover(RuntimeException e, EmployeeApiRequestDto employeeRequest) {
        logger.error("All retries failed for creating employee: {}", employeeRequest, e);
        throw new RuntimeException("Failed to create employee after retries: " + employeeRequest.getFirstName());
    }

    private EmployeeApiResponseDto mapApp2ResponseToApiResponse(EmployeeDataResponseDto app2Response) {
        return employeeMapper.toApp1Response(app2Response);
    }
    private void validateApp2Response(EmployeeDataResponseDto app2Response) {
        if (app2Response == null) {
            throw new RuntimeException("Invalid response from App2: Response is null.");
        }
        if (app2Response.getId() == null || app2Response.getName() == null || app2Response.getRoleId() == null) {
            throw new RuntimeException("Invalid response from App2: Required fields are missing.");
        }
    }

    private Long mapRoleToRoleId(String role) {
        return switch (role.toUpperCase()) {
            case "ADMIN" -> 1L;
            case "USER" -> 2L;
            case "MANAGER" -> 3L;
            default -> throw new IllegalArgumentException("Invalid role: " + role);
        };
    }

}
