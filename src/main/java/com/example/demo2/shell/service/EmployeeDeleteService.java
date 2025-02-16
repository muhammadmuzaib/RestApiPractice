package com.example.demo2.shell.service;

import org.springframework.http.ResponseEntity;

public interface EmployeeDeleteService {

    /**
     * Deletes an employee by username.
     *
     * @param username      the username of the employee to delete
     * @param correlationId the correlation ID for logging/tracking
     * @return a ResponseEntity with a success message or an error response if not found
     */
    ResponseEntity<?> deleteEmployee(String username, String correlationId);
}
