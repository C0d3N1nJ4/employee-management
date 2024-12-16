package com.naiomi.employee.api.model;

import com.naiomi.employee.api.exception.InvalidRoleException;

import java.util.Arrays;

public enum Role {
    ADMIN, USER, MANAGER;

    /**
     * Validate and get Role from a string.
     *
     * @param role  the role string from the header.
     * @return the corresponding Role enum value.
     * @throws InvalidRoleException if the role is invalid.
     */
    public static Role fromString(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role is required and cannot be null or blank. Allowed roles are ADMIN, USER, MANAGER.");
        }
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role + ". Allowed roles are ADMIN, USER, MANAGER");
        }
    }


}
