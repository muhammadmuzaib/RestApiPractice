package com.example.demo2.shell.service;

import com.example.demo2.shell.dto.request.EmployeeLoginRequestDto;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {

    /**
     * Handles the authentication logic for an employee login request.
     *
     * @param request       the login request DTO containing username and password
     * @param correlationId the correlation identifier for logging and tracking
     * @return a ResponseEntity with either a success response or an error response
     */
    ResponseEntity<?> handleAuthentication(EmployeeLoginRequestDto request, String correlationId);
}
