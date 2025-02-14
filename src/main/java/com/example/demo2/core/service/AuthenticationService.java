package com.example.demo2.core.service;

import com.example.demo2.shell.dto.request.EmployeeLoginRequestDto;
import com.example.demo2.shell.dto.response.LoginSuccessResponse;
import com.networknt.schema.ValidationMessage;
import com.networknt.schema.JsonSchema;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import com.example.demo2.shell.dto.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthenticationService {

    private static final Logger logger = LogManager.getLogger(AuthenticationService.class);

    private static final String SCHEMA_PATH = "/schemas/login-schema.json";

    private final SchemaValidationService schemaValidator;
    private final JsonResponseService responseService;
    private final EmployeeServiceImpl employeeServiceImpl;
    private final JsonSchema schema;

    @Autowired
    public AuthenticationService(SchemaValidationService schemaValidator,
                                 JsonResponseService responseService,
                                 EmployeeServiceImpl employeeServiceImpl) throws Exception {
        this.schemaValidator = schemaValidator;
        this.responseService = responseService;
        this.employeeServiceImpl = employeeServiceImpl;
        this.schema = schemaValidator.loadSchema(SCHEMA_PATH);
    }

    public ResponseEntity<?> validateRequest(String rawJson, String correlationId) {
        Set<ValidationMessage> errors = schemaValidator.validate(schema, rawJson);
        if (!errors.isEmpty()) {
            logger.error("Validation errors for correlationId {}: {}", correlationId, errors);
            return responseService.validationErrorResponse(errors, correlationId);
        }
        return null;
    }

    public EmployeeLoginRequestDto convertToDto(String rawJson, String correlationId) {
        try {
            EmployeeLoginRequestDto dto = responseService.parseJsonToDto(rawJson, EmployeeLoginRequestDto.class);
            logger.info("Parsed EmployeeLoginRequestDto: {}", dto);
            return dto;
        } catch (Exception e) {
            logger.error("Error parsing JSON for correlationId {}: {}", correlationId, e.getMessage());
            return null;
        }
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
