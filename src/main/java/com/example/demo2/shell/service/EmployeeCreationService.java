package com.example.demo2.shell.service;

import com.example.demo2.shell.dto.request.EmployeeCreateRequestDto;
import org.springframework.http.ResponseEntity;

public interface EmployeeCreationService {

    /**
     * Handles the employee creation logic.
     *
     * @param username      the employee's username
     * @param requestDto    the employee creation request DTO
     * @param correlationId the correlation ID for logging/tracking
     * @return a ResponseEntity indicating the result of the creation attempt
     */
    ResponseEntity<?> handleEmployeeCreation(String username, EmployeeCreateRequestDto requestDto, String correlationId);
}
