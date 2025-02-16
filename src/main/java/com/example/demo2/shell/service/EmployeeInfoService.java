package com.example.demo2.shell.service;

import org.springframework.http.ResponseEntity;

public interface EmployeeInfoService {

    /**
     * Retrieves employee information and builds a ResponseEntity.
     *
     * @param username      the employee's username
     * @param correlationId the correlation ID for logging and tracking
     * @return a ResponseEntity containing either the employee info or an error response
     */
    ResponseEntity<?> retrieveEmployeeInfo(String username, String correlationId);
}
