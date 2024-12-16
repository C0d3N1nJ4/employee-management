package com.naiomi.employee.api.config;

import com.naiomi.employee.api.security.RoleValidationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * In-memory user with ADMIN authority:
     * - username: user
     * - password: d1b82d02-3868-4575-8ca6-41abc3e9edd2
     * - authority: "ADMIN"
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // For demo: store the password in {noop}<plaintext> format
        UserDetails adminUser = User.withUsername("user")
                .password("{noop}d1b82d02-3868-4575-8ca6-41abc3e9edd2")
                .authorities("ADMIN") // literal "ADMIN"
                .build();

        return new InMemoryUserDetailsManager(adminUser);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // (1) Disable CSRF (simplifies testing)
        http.csrf(csrf -> csrf.disable());

        // (2) Add your custom RoleValidationFilter
        http.addFilterBefore(new RoleValidationFilter(),
                UsernamePasswordAuthenticationFilter.class);

        // (3) Configure Authorization
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/employees").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/employees/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.GET, "/employees/**").hasAnyAuthority("ADMIN", "USER")
                .requestMatchers(HttpMethod.PUT, "/employees/**").hasAnyAuthority("ADMIN", "USER")
                .anyRequest().permitAll()
        );

        // (4) Use a custom AccessDeniedHandler for 403 responses
        http.exceptionHandling(exception -> exception
                .accessDeniedHandler(customAccessDeniedHandler())
        );

        // (5) Enable Basic Auth
        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, ex) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            String jsonBody = "{\"error\":\"Access Denied\"}";
            response.getWriter().write(jsonBody);
        };
    }
}
