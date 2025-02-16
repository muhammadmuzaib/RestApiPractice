package com.example.demo2.core.service;

import com.example.demo2.shell.dto.response.SuccessResponse;
import com.example.demo2.shell.service.EmployeeDeleteService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class EmployeeDeleteServiceImpl implements EmployeeDeleteService {

    private static final Logger logger = LogManager.getLogger(EmployeeDeleteServiceImpl.class);

    private final EmployeeServiceImpl employeeServiceImpl;
    private final JsonResponseServiceImpl responseService;

    @Autowired
    public EmployeeDeleteServiceImpl(EmployeeServiceImpl employeeServiceImpl, JsonResponseServiceImpl responseService) {
        this.employeeServiceImpl = employeeServiceImpl;
        this.responseService = responseService;
    }

    @Override
    public ResponseEntity<?> deleteEmployee(String username, String correlationId) {
        logger.info("Processing delete for employee '{}'. CorrelationId: {}", username, correlationId);

        boolean exists = employeeServiceImpl.employeeExists(username);
        if (!exists) {
            logger.error("Employee '{}' not found. CorrelationId: {}", username, correlationId);
            return responseService.notFoundResponse(correlationId);
        }

        employeeServiceImpl.deleteEmployee(username);
        logger.info("Employee '{}' deleted successfully. CorrelationId: {}", username, correlationId);

        return ResponseEntity.ok(new SuccessResponse(
                "success",
                "Employee deleted successfully",
                correlationId
        ));
    }
}
