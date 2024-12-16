package com.naiomi.employee.api.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "retry.max-attempts=3",
        "retry.backoff-delay=1000"
})
class RetryConfigTest {

    @Value("${retry.max-attempts}")
    private int maxAttempts;

    @Value("${retry.backoff-delay}")
    private long backoffDelay;

    private RetryConfig retryConfig;

    @BeforeEach
    void setUp() {
        retryConfig = new RetryConfig();
    }

    @Test
    @DisplayName("Test RetryTemplate Behavior")
    void testRetryTemplateBehavior() {
        // Act
        RetryTemplate retryTemplate = retryConfig.retryTemplate(maxAttempts, backoffDelay);

        // Simulate retries
        long startTime = System.currentTimeMillis();
        int[] attemptCounter = {0};

        try {
            retryTemplate.execute((RetryCallback<Void, RuntimeException>) context -> {
                attemptCounter[0]++;
                if (attemptCounter[0] < maxAttempts) {
                    throw new RuntimeException("Simulated failure");
                }
                return null;
            });
        } catch (RuntimeException e) {
            // This should not happen since it succeeds on the last attempt
            fail("Retries exhausted before reaching max attempts");
        }

        long endTime = System.currentTimeMillis();

        // Assert
        assertEquals(maxAttempts, attemptCounter[0], "Number of attempts should match maxAttempts");
        assertTrue(endTime - startTime >= backoffDelay * (maxAttempts - 1),
                "Total retry duration should respect the backoff delay");
    }

    @Test
    @DisplayName("Test RetryTemplate Exhausts Retries")
    void testRetryTemplateExhaustRetries() {
        // Act
        RetryTemplate retryTemplate = retryConfig.retryTemplate(maxAttempts, backoffDelay);

        int[] attemptCounter = {0};

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                retryTemplate.execute((RetryCallback<Void, RuntimeException>) context -> {
                    attemptCounter[0]++;
                    throw new RuntimeException("Simulated failure");
                })
        );

        // Assert
        assertEquals(maxAttempts, attemptCounter[0], "Number of attempts should match maxAttempts");
        assertEquals("Simulated failure", exception.getMessage(), "Exception message should match");
    }
}
