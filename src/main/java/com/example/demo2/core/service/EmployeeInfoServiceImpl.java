package com.example.demo2.core.service;

import com.example.demo2.core.model.Employee;
import com.example.demo2.shell.dto.response.EmployeeInfoResponse;
import com.example.demo2.shell.service.EmployeeInfoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class EmployeeInfoServiceImpl implements EmployeeInfoService {

    private static final Logger logger = LogManager.getLogger(EmployeeInfoServiceImpl.class);

    private final EmployeeServiceImpl employeeServiceImpl;
    private final JsonResponseServiceImpl responseService;

    @Autowired
    public EmployeeInfoServiceImpl(EmployeeServiceImpl employeeServiceImpl,
                                   JsonResponseServiceImpl responseService) {
        this.employeeServiceImpl = employeeServiceImpl;
        this.responseService = responseService;
    }

    @Override
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
