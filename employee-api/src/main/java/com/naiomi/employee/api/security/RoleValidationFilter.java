package com.naiomi.employee.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class RoleValidationFilter extends OncePerRequestFilter {

    private static final Set<String> VALID_ROLES = Set.of("ADMIN", "USER", "MANAGER");
    private static final String INVALID_ROLE_MSG =
            "Invalid role: %s. Allowed roles are: [ADMIN, USER, MANAGER]";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String roleHeader = request.getHeader("Role");

        // 1. Validate the existence of the Role header
        if (!StringUtils.hasText(roleHeader)) {
            writeJsonError(response, HttpStatus.BAD_REQUEST,
                    "Role is required and cannot be null or blank. Allowed roles are ADMIN, USER, MANAGER.");
            return;
        }


        // 2. Validate length constraints (3 to 50 characters)
        if (roleHeader.length() < 3 || roleHeader.length() > 50) {
            writeJsonError(response, HttpStatus.BAD_REQUEST,
                    "Invalid 'Role' length. Must be between 3 and 50 characters.");
            return;
        }

        // 3. Validate role is in the allowed set
        if (!VALID_ROLES.contains(roleHeader.toUpperCase())) {
            // Use the unified message
            writeJsonError(response, HttpStatus.BAD_REQUEST,
                    String.format(INVALID_ROLE_MSG, roleHeader));
            return;
        }

        // 4. Role is valid -> set up SecurityContext
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleHeader.toUpperCase());
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken("anonymousUser", null, List.of(authority));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    private void writeJsonError(HttpServletResponse response, HttpStatus status, String errorMessage)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        String jsonError = String.format("{\"message\": \"%s\"}", errorMessage);
        response.getWriter().write(jsonError);
        response.flushBuffer();
    }
}
