package com.naiomi.employee.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableRetry
public class RetryConfig {

    @Bean
    public RetryTemplate retryTemplate(
            @Value("${retry.max-attempts}") int maxAttempts,
            @Value("${retry.backoff-delay}") long backoffDelay) {
        RetryTemplate retryTemplate = new RetryTemplate();

        // Set the retry policy
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(maxAttempts);
        retryTemplate.setRetryPolicy(retryPolicy);

        // Set the backoff policy
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(backoffDelay);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }
}
