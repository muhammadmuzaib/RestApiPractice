package com.example.demo2.shell.service;

import com.example.demo2.shell.dto.request.EmployeeUpdateRequestDto;
import org.springframework.http.ResponseEntity;

public interface EmployeeUpdateService {

    /**
     * Handles the employee update process.
     *
     * @param username      the employee's username
     * @param request       the DTO containing update details
     * @param correlationId the correlation identifier for logging/tracking
     * @return a ResponseEntity indicating the outcome of the update
     */
    ResponseEntity<?> handleEmployeeUpdate(String username, EmployeeUpdateRequestDto request, String correlationId);
}
