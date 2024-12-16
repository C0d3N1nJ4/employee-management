package com.naiomi.employee.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception for invalid roles in employee operations.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRoleException extends RuntimeException {

    /**
     * Constructs a new InvalidRoleException with the specified detail message.
     *
     * @param message the detail message.
     */
    public InvalidRoleException(String message) {
        super(message);
    }
}
