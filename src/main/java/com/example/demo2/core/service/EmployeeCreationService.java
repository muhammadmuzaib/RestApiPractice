package com.example.demo2.core.service;

import com.example.demo2.shell.dto.request.EmployeeCreateRequestDto;
import com.example.demo2.shell.dto.response.SuccessResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class EmployeeCreationService {

    private static final Logger logger = LogManager.getLogger(EmployeeCreationService.class);

    private final EmployeeServiceImpl employeeServiceImpl;
    private final JsonResponseService responseService;

    @Autowired
    public EmployeeCreationService(EmployeeServiceImpl employeeServiceImpl, JsonResponseService responseService) {
        this.employeeServiceImpl = employeeServiceImpl;
        this.responseService = responseService;
    }

    /**
     * Handles the employee creation logic.
     *
     * @param username      the employee's username
     * @param requestDto    the employee creation request DTO
     * @param correlationId the correlation ID for logging/tracking
     * @return a ResponseEntity indicating the result of the creation attempt
     */
    public ResponseEntity<?> handleEmployeeCreation(String username, EmployeeCreateRequestDto requestDto, String correlationId) {
        logger.info("Handling employee creation for username: {}. CorrelationId: {}", username, correlationId);

        if (employeeServiceImpl.employeeExists(username)) {
            logger.error("Conflict: Employee already exists for username: {}. CorrelationId: {}", username, correlationId);
            return responseService.conflictErrorResponse("Employee already exists", correlationId);
        }

        employeeServiceImpl.createEmployee(
                username,
                requestDto.getPassword(),
                requestDto.getFirstName(),
                requestDto.getLastName()
        );
        logger.info("Employee created successfully for username: {}. CorrelationId: {}", username, correlationId);
        return ResponseEntity.ok(new SuccessResponse("success", "Employee created", correlationId));
    }
}
