package com.example.demo2.core.service;

import com.example.demo2.shell.dto.request.EmployeeLoginRequestDto;
import com.example.demo2.shell.dto.response.LoginSuccessResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import com.example.demo2.shell.dto.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class AuthenticationService {

    private static final Logger logger = LogManager.getLogger(AuthenticationService.class);

    private final EmployeeServiceImpl employeeServiceImpl;

    @Autowired
    public AuthenticationService(EmployeeServiceImpl employeeServiceImpl) {
        this.employeeServiceImpl = employeeServiceImpl;
    }

    public ResponseEntity<?> handleAuthentication(EmployeeLoginRequestDto request, String correlationId) {
        logger.info("Attempting authentication for user: {}, Correlation ID: {}", request.getUsername(), correlationId);
        boolean isAuthenticated = authenticateUser(
                request.getUsername(),
                request.getPassword(),
                correlationId
        );

        if (isAuthenticated) {
            logger.info("User {} authenticated successfully. Correlation ID: {}", request.getUsername(), correlationId);
            return ResponseEntity.ok(new LoginSuccessResponse(
                    "success",
                    "User authenticated",
                    correlationId
            ));
        } else {
            logger.error("Authentication failed for user {}. Correlation ID: {}", request.getUsername(), correlationId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("error", "Invalid credentials", correlationId, null));
        }
    }

    public boolean authenticateUser(String username, String password, String correlationId) {
        logger.info("Authenticating user: {}. Correlation ID: {}", username, correlationId);
        return employeeServiceImpl.isValidEmployee(username, password, correlationId);
    }
}
