package com.example.demo2.core.service;

import com.example.demo2.core.model.Employee;
import com.example.demo2.shell.dto.response.EmployeeInfoResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class EmployeeInfoService {

    private static final Logger logger = LogManager.getLogger(EmployeeInfoService.class);

    private final EmployeeServiceImpl employeeServiceImpl;
    private final JsonResponseService responseService;

    @Autowired
    public EmployeeInfoService(EmployeeServiceImpl employeeServiceImpl,
                               JsonResponseService responseService) {
        this.employeeServiceImpl = employeeServiceImpl;
        this.responseService = responseService;
    }

    /**
     * Retrieves employee information and builds a ResponseEntity.
     *
     * @param username      the employee's username
     * @param correlationId the correlation ID for logging and tracking
     * @return a ResponseEntity containing either the employee info or an error response
     */
    public ResponseEntity<?> retrieveEmployeeInfo(String username, String correlationId) {
        logger.info("Processing request for username: {}. CorrelationId: {}", username, correlationId);

        Employee employee = employeeServiceImpl.getEmployeeByUsername(username);

        if (employee == null) {
            logger.error("Employee not found for username: {}. CorrelationId: {}", username, correlationId);
            return responseService.notFoundResponse(correlationId);
        }

        logger.info("Employee found: {} {}. CorrelationId: {}",
                employee.getFirstName(), employee.getLastName(), correlationId);

        EmployeeInfoResponse response = new EmployeeInfoResponse(
                employee.getFirstName(),
                employee.getLastName(),
                correlationId
        );

        return ResponseEntity.ok(response);
    }
}
