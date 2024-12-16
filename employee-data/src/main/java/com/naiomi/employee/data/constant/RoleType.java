package com.naiomi.employee.data.constant;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enum representing the different types of roles in the employee system.
 */
public enum RoleType {
    ADMIN,   // Represents an admin role
    USER,    // Represents a regular user role
    MANAGER; // Represents a manager role

    /**
     * Utility method to validate if a role exists in the RoleType enum.
     *
     * @param role The role to validate.
     * @return {@code true} if the role exists in the enum, {@code false} otherwise.
     */
    public static boolean isValid(String role) {
        return Stream.of(RoleType.values())
                .map(Enum::name)
                .collect(Collectors.toSet())
                .contains(role);
    }

    /**
     * Utility method to retrieve all roles as a Set of Strings.
     *
     * @return A Set of all roles defined in the RoleType enum.
     */
    public static Set<String> getAllRoles() {
        return Stream.of(RoleType.values())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }
}
