package com.example.demo2.core.service;

import com.example.demo2.core.model.Employee;
import com.example.demo2.shell.dto.request.EmployeeUpdateRequestDto;
import com.example.demo2.shell.dto.response.SuccessResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class EmployeeUpdateService {

    private static final Logger logger = LogManager.getLogger(EmployeeUpdateService.class);

    private final EmployeeServiceImpl employeeServiceImpl;
    private final JsonResponseService responseService;

    public EmployeeUpdateService(EmployeeServiceImpl employeeServiceImpl, JsonResponseService responseService) {
        this.employeeServiceImpl = employeeServiceImpl;
        this.responseService = responseService;
    }

    /**
     * Handles the employee update process.
     *
     * @param username      the employee's username
     * @param request       the DTO containing update details
     * @param correlationId the correlation identifier for logging/tracking
     * @return a ResponseEntity indicating the outcome of the update
     */
    public ResponseEntity<?> handleEmployeeUpdate(String username,
                                                  EmployeeUpdateRequestDto request,
                                                  String correlationId) {
        logger.info("Handling employee update for username: {}. CorrelationId: {}", username, correlationId);

        Employee employee = employeeServiceImpl.getEmployeeByUsername(username);
        if (employee == null) {
            logger.error("Employee not found for update (username: {}, correlationId: {})", username, correlationId);
            return responseService.notFoundResponse(correlationId);
        }

        employeeServiceImpl.updateEmployee(username, request);
        logger.info("Employee updated successfully for username: {}. CorrelationId: {}", username, correlationId);

        return ResponseEntity.ok(new SuccessResponse(
                "success",
                "Employee updated successfully",
                correlationId
        ));
    }
}
